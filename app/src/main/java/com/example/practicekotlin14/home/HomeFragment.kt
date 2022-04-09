package com.example.practicekotlin14.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practicekotlin14.DBKey.Companion.CHILD_CHAT
import com.example.practicekotlin14.DBKey.Companion.DB_ARTICLES
import com.example.practicekotlin14.DBKey.Companion.DB_USERS
import com.example.practicekotlin14.R
import com.example.practicekotlin14.chatlist.ChatListItem
import com.example.practicekotlin14.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var articleDB: DatabaseReference
    private lateinit var userDB: DatabaseReference

    private val articleList = mutableListOf<ArticleModel>()
    private val listener = object : ChildEventListener {
        // fragment 는 재사용 되기 때문에 해당 fragment 일일히 호출하게 되면 화면으로 돌아올 때 마다 위에 덮어질 우려가 있으므로 전역 변수로 선언해둔 뒤에 함수를 통해 재사용한다.
        override fun onChildAdded(
            snapshot: DataSnapshot,
            previousChildName: String?
        ) { // snaption 하나하나가 articleModel, snaption을 articleModel로 치환 해준 뒤 submitList() 사용

            val articleModel =
                snapshot.getValue(ArticleModel::class.java) // map을 만들어 속성 하나하나를 대입하는 것이 아닌 모델 클래스 자체를 업로드하여 다운받아, articleModel 자체를 인스턴스화하여 매핑
            articleModel ?: return

            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}

    }

    private var binding: FragmentHomeBinding? = null

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        articleList.clear()
        // clear를 하는 이유는 뷰가 재사용 되고있는중 articleList는 값을 계속 저장하고 있어
        // 값이 계속해서 쌓이기 때문에 clear를 선언하지 않으면 하나의 값이 여러번 나오는 경우가 발생하기 때문에 clear() 호출
        userDB = Firebase.database.reference.child(DB_USERS)
        articleDB = Firebase.database.reference.child(DB_ARTICLES)
        articleAdapter = ArticleAdapter(onItemClicked = { articleModel -> // 리사이클러 뷰 항목을 누르면 chat 으로 이동
            if (auth.currentUser != null) {
                // 로그인을 한 상태
                if (auth.currentUser.uid != articleModel.sellerId) {

                    val chatRoom = ChatListItem(
                        buyerId = auth.currentUser.uid,
                        sellerId = articleModel.sellerId,
                        itemTitle = articleModel.title,
                        key = System.currentTimeMillis()
                    )

                    userDB.child(auth.currentUser.uid) // 사용자 db 설정
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    userDB.child(articleModel.sellerId) // 타 사용자 db 설정
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    Snackbar.make(view, "채팅방이 생성되었습니다. 채팅탭에서 확인해주세요.", Snackbar.LENGTH_SHORT).show()


               } else {
                    // 내가 올린 아이템
                    Snackbar.make(view, "내가 올린 아이템입니다.", Snackbar.LENGTH_LONG).show()
                }
            } else {
                // 로그인을 안한 상태
                Snackbar.make(view, "로그인 후 사용해주세요.", Snackbar.LENGTH_LONG).show()
            }
        })


        fragmentHomeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerView.adapter = articleAdapter


        // requireContext를 사용하는 이유는 그냥 context를 사용해도 무방하나 null의 가능성이 있기 때문에 requiureContext를 사용
        //             context?.let {
        //                startActivity(Intent(it, ArticleAddActivity::class.java))
        //            } <- 이렇게 사용해도 가능

        fragmentHomeBinding.addFloatingButton.setOnClickListener {
            if (auth.currentUser != null) {
                val intent = Intent(requireContext(), AddArticleActivity::class.java)
                startActivity(intent)
            } else {
                Snackbar.make(view, "로그인 후 사용해주세요.", Snackbar.LENGTH_LONG).show()
            }
        }

        articleDB.addChildEventListener(listener)
    }

    override fun onResume() { // 포그라운드 복귀 시 뷰를 다시 그림
        super.onResume()

        articleAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()

        articleDB.removeEventListener(listener)
    }
}