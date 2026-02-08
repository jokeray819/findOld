<template>
  <view class="page">
    <view class="tabs">
      <view
        class="tab"
        :class="{ active: tab === 'list' }"
        @tap="tab = 'list'; reload()"
      >
        达人列表
      </view>
      <view
        class="tab"
        :class="{ active: tab === 'followings' }"
        @tap="tab = 'followings'; reload()"
      >
        我关注的
      </view>
    </view>
    <view class="apply-section" v-if="tab === 'list' && expertMe">
      <view class="me-status">
        我的认证：{{ expertMe.status === 'certified' ? '已认证' : expertMe.status === 'pending' ? '审核中' : '未认证' }}
        （有效帮寻 {{ expertMe.validHelpCount || 0 }} 次）
      </view>
      <button
        v-if="expertMe.status !== 'certified' && expertMe.status !== 'pending'"
        class="apply-btn"
        size="mini"
        @tap="applyExpert"
      >
        申请达人认证
      </button>
    </view>
    <scroll-view
      scroll-y
      class="list-scroll"
      :style="{ height: scrollHeight + 'px' }"
      @scrolltolower="loadMore"
    >
      <view class="list-inner">
        <view v-for="e in list" :key="e.userId" class="expert-card">
          <image
            v-if="e.avatarUrl"
            class="avatar"
            :src="e.avatarUrl"
            mode="aspectFill"
          />
          <view v-else class="avatar placeholder">头</view>
          <view class="info">
            <text class="name">{{ e.nickname || '达人' }}</text>
            <text class="count">有效帮寻 {{ e.validHelpCount || 0 }} 次</text>
            <text v-if="e.status === 'certified'" class="cert">已认证</text>
          </view>
          <button
            v-if="tab === 'list'"
            class="follow-btn"
            size="mini"
            @tap="follow(e.userId)"
          >
            关注
          </button>
          <button
            v-if="tab === 'followings'"
            class="unfollow-btn"
            size="mini"
            @tap="unfollow(e.userId)"
          >
            取消关注
          </button>
        </view>
        <view v-if="loading" class="tip">加载中...</view>
        <view v-else-if="noMore && list.length" class="tip">没有更多了</view>
        <view v-else-if="!loading && !list.length" class="empty">暂无达人</view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { listExperts, getExpertMe, applyExpert as apiApplyExpert } from '@/api/expert.js'
import { listMyFollowings, followExpert, unfollowExpert } from '@/api/follow.js'

const tab = ref('list')
const list = ref([])
const page = ref(0)
const size = 20
const loading = ref(false)
const noMore = ref(false)
const scrollHeight = ref(400)
const expertMe = ref(null)

onLoad((q) => {
  if (q?.tab === 'followings') tab.value = 'followings'
})

async function loadExpertMe() {
  try {
    expertMe.value = await getExpertMe()
  } catch (_) {
    expertMe.value = null
  }
}

async function fetchList() {
  if (loading.value) return
  loading.value = true
  try {
    let res
    if (tab.value === 'followings') {
      res = await listMyFollowings(page.value, size)
    } else {
      res = await listExperts({ page: page.value, size, status: 'certified' })
    }
    const content = res.content || []
    if (page.value === 0) list.value = content
    else list.value = list.value.concat(content)
    noMore.value = res.last !== undefined ? res.last : list.value.length >= (res.totalElements ?? 0)
  } catch (_) {}
  finally {
    loading.value = false
  }
}

function reload() {
  page.value = 0
  noMore.value = false
  list.value = []
  fetchList()
  if (tab.value === 'list') loadExpertMe()
}

function loadMore() {
  if (loading.value || noMore.value) return
  page.value += 1
  fetchList()
}

async function follow(userId) {
  try {
    await followExpert(userId)
    uni.showToast({ title: '已关注' })
  } catch (_) {}
}

async function unfollow(userId) {
  try {
    await unfollowExpert(userId)
    uni.showToast({ title: '已取消关注' })
    list.value = list.value.filter((e) => e.userId !== userId)
  } catch (_) {}
}

async function applyExpert() {
  try {
    await apiApplyExpert()
    uni.showToast({ title: '已提交申请' })
    loadExpertMe()
  } catch (_) {}
}

onMounted(() => {
  const sys = uni.getSystemInfoSync()
  scrollHeight.value = (sys.windowHeight || 400) - 180
  if (tab.value === 'followings') fetchList()
  else {
    loadExpertMe()
    fetchList()
  }
})

</script>

<style lang="scss" scoped>
.page { background: #f5f5f5; min-height: 100vh; }
.tabs {
  display: flex;
  background: #fff;
  padding: 0 24rpx;
  .tab {
    padding: 28rpx 32rpx;
    font-size: 28rpx;
    color: #666;
    &.active { color: #07c160; font-weight: bold; border-bottom: 4rpx solid #07c160; }
  }
}
.apply-section {
  padding: 24rpx;
  background: #fff;
  margin-bottom: 16rpx;
  .me-status { font-size: 26rpx; color: #666; margin-bottom: 16rpx; }
  .apply-btn { background: #07c160; color: #fff; border: none; }
}
.list-scroll { padding: 24rpx; }
.list-inner { padding-bottom: 48rpx; }
.expert-card {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
  .avatar {
    width: 88rpx;
    height: 88rpx;
    border-radius: 50%;
    margin-right: 24rpx;
  }
  .avatar.placeholder {
    background: #ddd;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32rpx;
    color: #999;
  }
  .info { flex: 1; .name { display: block; font-size: 30rpx; font-weight: bold; } .count, .cert { font-size: 24rpx; color: #999; margin-right: 12rpx; } }
  .follow-btn { background: #07c160; color: #fff; border: none; }
  .unfollow-btn { background: #f0f0f0; color: #666; border: none; }
}
.tip, .empty { text-align: center; padding: 32rpx; color: #999; font-size: 26rpx; }
</style>
