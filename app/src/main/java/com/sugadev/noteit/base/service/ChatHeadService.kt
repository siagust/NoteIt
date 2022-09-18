package com.sugadev.noteit.base.service

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import com.sugadev.noteit.MainActivity
import com.sugadev.noteit.R


class ChatHeadService : Service() {
    private var mWindowManager: WindowManager? = null
    private var mChatHeadView: View? = null

    private var startX = 0
    private var startY = 0
    private var initialTouchX = 0
    private var initialTouchY = 0
    private var lastAction = 0
    private val SNAP_STEPS = 50
    private val params = getLayoutParams()

    fun getLayoutParams() = LayoutParams(
        LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT,
        getParamType(),
        LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )

    fun getParamType() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        //Inflate the chat head layout we created
        mChatHeadView = LayoutInflater.from(this).inflate(R.layout.layout_chat_head, null)

        //Add the view to the window.

        //Specify the chat head position
        params.gravity =
            Gravity.TOP or Gravity.LEFT //Initially view will be added to top-left corner
        params.x = 0
        params.y = 100

        //Add the view to the window
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager?
        mWindowManager!!.addView(mChatHeadView, params)

        //Set the close button.
//        val closeButton: ImageView = mChatHeadView?.findViewById(id.close_btn) as ImageView
//        closeButton.setOnClickListener { //close the service and remove the chat head from the window
//            stopSelf()
//        }

        //Drag and move chat head using user's touch action.
        mChatHeadView?.setOnTouchListener(View.OnTouchListener { _, event ->

            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = params.x
                    startY = params.y
                    initialTouchX = event.rawX.toInt()
                    initialTouchY = event.rawY.toInt()
                    lastAction = event.action

                    shrinkEffect(1.0f, 0.9f)
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    if (lastAction == MotionEvent.ACTION_DOWN) {
                        launchApp()
                    } else {
                        shrinkEffect(0.9f, 1.0f)
                        snapToEdge(params.x, getNearestEdge(params.x), params.y)
                    }
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_MOVE -> {
                    drag(event.rawX, event.rawY)
                    lastAction = event.action
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    private fun shrinkEffect(from: Float, to: Float) {
        mChatHeadView?.scaleAnimator(from, to, 50, AccelerateInterpolator())?.start()
    }

    private fun getNearestEdge(x: Int): Int {
        val displayMetrics = DisplayMetrics()
        mWindowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val center = displayMetrics.widthPixels - (getWidth() / 2)
        val screenWidth = displayMetrics.widthPixels

        return if (x < center / 2) {
            0
        } else {
            screenWidth - getWidth()
        }
    }

    fun getWidth() = mChatHeadView?.measuredWidth ?: 0

    fun View.scaleAnimator(
        from: Float,
        to: Float,
        duration: Long = 500,
        interpolator: Interpolator = LinearInterpolator()
    ): Animator {
        val animator = ValueAnimator.ofFloat(from, to)
        animator.addUpdateListener {
            scaleX = it.animatedValue as Float
            scaleY = it.animatedValue as Float
        }
        animator.duration = duration
        animator.interpolator = interpolator
        return animator
    }

    private fun snapToEdge(startX: Int, x: Int, y: Int) {
        val animator = ValueAnimator.ofInt(0, SNAP_STEPS)
            .setDuration(350)
        animator.interpolator = OvershootInterpolator(1.3f)
        val windowSize = Point()
        mWindowManager?.defaultDisplay?.getSize(windowSize)
        val sizeX = windowSize.x
        animator.addUpdateListener {
            val currentX = startX + ((x - startX) * it.animatedValue as Int / SNAP_STEPS)

            params.x = currentX
            params.x = if (params.x < 0) {
                0
            } else {
                params.x
            }
            params.x = if (params.x > sizeX - (mChatHeadView?.width ?: 0)) {
                sizeX - (mChatHeadView?.width ?: 0)
            } else {
                params.x
            }

            params.y = y
            params.y = if (params.y < 0) {
                0
            } else {
                params.y
            }

            updateViewLayout()
        }

        animator.start()
    }

    private fun drag(rawX: Float, rawY: Float) {
        params.x = startX + Math.round(rawX - initialTouchX)
        params.y = startY + Math.round(rawY - initialTouchY)
        updateViewLayout()
    }

    private fun updateViewLayout() {
        if (mChatHeadView?.windowToken != null) {
            mWindowManager?.updateViewLayout(mChatHeadView, params)
        }
    }

    fun launchApp() {
        val intent = Intent(this@ChatHeadService, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mChatHeadView != null) mWindowManager!!.removeView(mChatHeadView)
    }
}
