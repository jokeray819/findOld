<template>
  <view class="user-center">
    <view v-if="!userStore.isLoggedIn" class="login-section">
      <button class="login-btn" @tap="onLoginTap">微信登录</button>
      <view class="login-tip">登录后可使用发帖、解答、赏金、收藏等功能</view>
    </view>
    <view v-else class="profile-section">
      <view class="profile-header">
        <image
          v-if="me.avatarUrl"
          class="avatar"
          :src="me.avatarUrl"
          mode="aspectFill"
        />
        <view v-else class="avatar avatar-placeholder">头</view>
        <view class="info">
          <text class="nickname">{{ me.nickname || '怀旧用户' }}</text>
          <view class="stats">
            <text>发帖 {{ me.postCount }}</text>
            <text>解答 {{ me.answerCount }}</text>
          </view>
        </view>
      </view>
      <view class="menu-list">
        <view class="menu-item" @tap="goMyPosts">
          <text>我的发帖</text>
          <text class="arrow">></text>
        </view>
        <view class="menu-item" @tap="goMyAnswers">
          <text>我的解答</text>
          <text class="arrow">></text>
        </view>
        <view class="menu-item" @tap="goBountyOrders">
          <text>赏金记录</text>
          <text class="arrow">></text>
        </view>
        <view class="menu-item" @tap="goFavorites">
          <text>我的收藏</text>
          <text class="arrow">></text>
        </view>
        <view class="menu-item" @tap="goFollowings">
          <text>关注的达人</text>
          <text class="arrow">></text>
        </view>
        <view class="menu-item" @tap="goExpertApply">
          <text>达人认证</text>
          <text class="arrow">></text>
        </view>
      </view>
      <view class="logout-wrap">
        <button class="logout-btn" @tap="logout">退出登录</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useUserStore } from '@/store/user.js'
import { getMe } from '@/api/user.js'

const userStore = useUserStore()
const me = ref({
  nickname: '',
  avatarUrl: '',
  postCount: 0,
  answerCount: 0,
  hasBountyOrders: false,
})

async function loadMe() {
  if (!userStore.isLoggedIn) return
  try {
    const res = await getMe()
    me.value = res
  } catch (_) {
    // 401 等已在 request 中处理
  }
}

function onLoginTap() {
  userStore.doWechatLogin().then((ok) => {
    if (ok) {
      me.value = { ...userStore.user, postCount: 0, answerCount: 0, hasBountyOrders: false }
      loadMe()
    }
  })
}

watch(() => userStore.user, (u) => {
  if (u) {
    me.value = { ...u, postCount: 0, answerCount: 0, hasBountyOrders: false }
    loadMe()
  }
}, { immediate: true })

onMounted(() => {
  if (userStore.isLoggedIn) loadMe()
})

function goMyPosts() {
  uni.navigateTo({ url: '/pages/my-posts/my-posts' })
}
function goMyAnswers() {
  uni.navigateTo({ url: '/pages/my-answers/my-answers' })
}
function goBountyOrders() {
  uni.navigateTo({ url: '/pages/bounty-orders/bounty-orders' })
}
function goFavorites() {
  uni.navigateTo({ url: '/pages/favorites/favorites' })
}
function goFollowings() {
  uni.navigateTo({ url: '/pages/expert-list/expert-list?tab=followings' })
}
function goExpertApply() {
  uni.navigateTo({ url: '/pages/expert-list/expert-list?tab=apply' })
}

function logout() {
  uni.showModal({
    title: '提示',
    content: '确定退出登录？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        me.value = { nickname: '', avatarUrl: '', postCount: 0, answerCount: 0, hasBountyOrders: false }
      }
    },
  })
}
</script>

<style lang="scss" scoped>
.user-center {
  min-height: 100vh;
  background: #f5f5f5;
}
.login-section {
  padding: 80rpx 48rpx;
  .login-btn {
    background: #07c160;
    color: #fff;
    border: none;
    border-radius: 12rpx;
    height: 88rpx;
    line-height: 88rpx;
    font-size: 32rpx;
  }
  .login-tip {
    margin-top: 24rpx;
    color: #999;
    font-size: 26rpx;
  }
}
.profile-section {
  padding: 24rpx;
}
.profile-header {
  background: #fff;
  border-radius: 16rpx;
  padding: 40rpx;
  display: flex;
  align-items: center;
  .avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    margin-right: 32rpx;
  }
  .avatar-placeholder {
    background: #ddd;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 40rpx;
    color: #999;
  }
  .nickname { font-size: 36rpx; font-weight: bold; display: block; margin-bottom: 12rpx; }
  .stats { font-size: 26rpx; color: #666; }
}
.menu-list {
  margin-top: 24rpx;
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
}
.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #eee;
  &:last-child { border-bottom: none; }
  .arrow { color: #999; }
}
.logout-wrap {
  margin-top: 48rpx;
  .logout-btn {
    background: #fff;
    color: #666;
    border: 1rpx solid #ddd;
    border-radius: 12rpx;
    height: 80rpx;
    line-height: 80rpx;
    font-size: 28rpx;
  }
}
</style>
