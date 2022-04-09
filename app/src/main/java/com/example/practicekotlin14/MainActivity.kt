package com.example.practicekotlin14

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.practicekotlin14.chatlist.ChatListFragment
import com.example.practicekotlin14.home.HomeFragment
import com.example.practicekotlin14.mypage.MyPageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val chatListFragment = ChatListFragment()
        val myPageFragment = MyPageFragment()


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        replaceFragment(homeFragment) // fragment 초기화

        bottomNavigationView.setOnNavigationItemSelectedListener { // bottomNavigation item 클릭 시, 이벤트
            when (it.itemId) {
                R.id.home -> replaceFragment(homeFragment)
                R.id.chatList -> replaceFragment(chatListFragment)
                R.id.myPage -> replaceFragment(myPageFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) { // 각각의 아이템 fragment 로 이동 시 메서드

        // fragment 의 활성화 시작을 알리는 함수
        supportFragmentManager.beginTransaction()
            .apply {
                replace(
                    R.id.fragmentContainer,
                    fragment
                ) // fragmentContainer 의 영역을 두 번째 인자인 fragment 로 교체
                commit() // fragment 활성화
            }

    }
}