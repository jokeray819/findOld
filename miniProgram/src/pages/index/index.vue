<template>
  <view class="page">
    <view class="load-bar" v-if="!list.length && !loading">
      <button class="load-btn" type="default" @tap="loadPostsDirect">点击加载帖子</button>
    </view>
    <view class="filter-bar">
      <view class="filter-item" @tap="openTypeSelect">
        <text>{{ typeLabel }}</text>
        <text class="arrow">▼</text>
      </view>
      <view class="filter-item" @tap="openEraSelect">
        <text>{{ eraLabel }}</text>
        <text class="arrow">▼</text>
      </view>
      <view class="filter-item" @tap="toggleBounty">
        <text>{{ bountyLabel }}</text>
        <text class="arrow">▼</text>
      </view>
      <view class="search-entry" @tap="goSearch">搜索</view>
      <view class="search-entry" @tap="goExperts">达人</view>
    </view>
    <scroll-view
      scroll-y
      class="list-scroll"
      :style="{ height: scrollHeight + 'px' }"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresherRefresh"
      @scrolltolower="loadMore"
    >
      <view class="list-inner">
        <post-card v-for="p in list" :key="p.id" :post="p" />
        <view v-if="loading" class="loading-tip">加载中...</view>
        <view v-else-if="noMore && list.length" class="loading-tip">没有更多了</view>
        <view v-else-if="!loading && !list.length" class="empty">暂无求助</view>
      </view>
    </scroll-view>
    <view class="publish-btn" @tap="goPublish">+ 发布求助</view>

  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad, onShow, onReady } from '@dcloudio/uni-app'
import { listPosts } from '@/api/post.js'

const list = ref([])
const page = ref(0)
const size = 20
const totalElements = ref(0)
const loading = ref(false)
const noMore = ref(false)
const scrollHeight = ref(400)
const refreshing = ref(false)

const typeOptions = [
  { value: '', label: '全部' },
  { value: 'toy', label: '玩具' },
  { value: 'stationery', label: '文具' },
  { value: 'snack', label: '零食' },
  { value: 'old_object', label: '老物件' },
]
const eraOptions = [
  { value: '', label: '全部' },
  { value: '80s', label: '80后' },
  { value: '90s', label: '90后' },
  { value: '00s', label: '00后' },
]

const filter = ref({
  type: '',
  sceneEra: '',
  bountyMode: '',
  tagIds: [],
})

const typeLabel = computed(() => {
  const o = typeOptions.find((x) => x.value === filter.value.type)
  return o ? o.label : '类型'
})
const eraLabel = computed(() => {
  const o = eraOptions.find((x) => x.value === filter.value.sceneEra)
  return o ? o.label : '年代'
})

const bountyLabel = computed(() => {
  if (!filter.value.bountyMode) return '赏金'
  if (filter.value.bountyMode === 'none') return '无赏金'
  return '有赏金'
})

function openTypeSelect() {
  uni.showActionSheet({
    itemList: typeOptions.map((o) => o.label),
    success: (res) => {
      filter.value.type = typeOptions[res.tapIndex].value
      reload()
    },
  })
}
function openEraSelect() {
  uni.showActionSheet({
    itemList: eraOptions.map((o) => o.label),
    success: (res) => {
      filter.value.sceneEra = eraOptions[res.tapIndex].value
      reload()
    },
  })
}
function toggleBounty() {
  if (!filter.value.bountyMode) filter.value.bountyMode = 'none'
  else if (filter.value.bountyMode === 'none') filter.value.bountyMode = 'escrow'
  else filter.value.bountyMode = ''
  reload()
}

function reload() {
  page.value = 0
  noMore.value = false
  list.value = []
  fetchList()
}

/** 直接发请求拉取帖子列表（与「点击加载帖子」同路径，保证能触发请求） */
function loadPostsDirect() {
  return new Promise((resolve) => {
    loading.value = true
    const token = (typeof wx !== 'undefined' ? wx.getStorageSync('findold_token') : uni.getStorageSync('findold_token')) || ''
    const header = { 'Content-Type': 'application/json' }
    if (token) header['Authorization'] = 'Bearer ' + token
    const requestApi = typeof wx !== 'undefined' ? wx : uni
    requestApi.request({
      url: 'http://localhost:8080/api/posts?page=0&size=20',
      method: 'GET',
      header,
      success: (res) => {
        if (res.statusCode === 401) {
          if (typeof wx !== 'undefined') wx.removeStorageSync('findold_token')
          else uni.removeStorageSync('findold_token')
          uni.showToast({ title: '请重新登录', icon: 'none' })
          loading.value = false
          resolve()
          return
        }
        if (res.statusCode >= 200 && res.statusCode < 300 && res.data) {
          const data = res.data
          const content = Array.isArray(data.content) ? data.content : []
          list.value = content
          totalElements.value = data.totalElements != null ? data.totalElements : 0
          noMore.value = data.last !== undefined ? data.last : list.value.length >= totalElements.value
        }
        loading.value = false
        resolve()
      },
      fail: (err) => {
        uni.showToast({ title: '网络错误', icon: 'none' })
        loading.value = false
        resolve()
      },
    })
  })
}

/** 下拉刷新：使用与按钮相同的请求方式 */
async function onRefresherRefresh() {
  refreshing.value = true
  page.value = 0
  noMore.value = false
  list.value = []
  try {
    await loadPostsDirect()
  } finally {
    refreshing.value = false
  }
}

async function fetchList() {
  if (loading.value) return
  loading.value = true
  try {
    const res = await listPosts({
      type: filter.value.type || undefined,
      sceneEra: filter.value.sceneEra || undefined,
      bountyMode: filter.value.bountyMode || undefined,
      tagIds: filter.value.tagIds.length ? filter.value.tagIds : undefined,
      page: page.value,
      size,
    })
    const content = res.content || []
    totalElements.value = res.totalElements ?? 0
    if (page.value === 0) list.value = content
    else list.value = list.value.concat(content)
    if (res.last !== undefined) noMore.value = res.last
    else noMore.value = list.value.length >= totalElements.value
  } catch (_) {}
  finally {
    loading.value = false
  }
}

function loadMore() {
  if (loading.value || noMore.value) return
  page.value += 1
  fetchList()
}

onLoad(() => {
  const sys = uni.getSystemInfoSync()
  scrollHeight.value = (sys.windowHeight || 400) - 120
  setTimeout(() => loadPostsDirect(), 50)
})

/** 每次显示页面都拉列表（含从「我的」切回首页） */
onShow(() => {
  const sys = uni.getSystemInfoSync()
  scrollHeight.value = (sys.windowHeight || 400) - 120
  loadPostsDirect()
})

onReady(() => {
  if (list.value.length === 0 && !loading.value) {
    loadPostsDirect()
  }
})

function goSearch() {
  uni.navigateTo({ url: '/pages/search/search' })
}
function goExperts() {
  uni.navigateTo({ url: '/pages/expert-list/expert-list' })
}
function goPublish() {
  uni.navigateTo({ url: '/pages/post-publish/post-publish' })
}
</script>

<style lang="scss" scoped>
.page { background: #f5f5f5; min-height: 100vh; }
.load-bar { padding: 24rpx; text-align: center; }
.load-btn { background: #07c160; color: #fff; border: none; font-size: 28rpx; padding: 16rpx 32rpx; border-radius: 12rpx; }
.filter-bar {
  display: flex;
  align-items: center;
  padding: 16rpx 24rpx;
  background: #fff;
  .filter-item, .search-entry {
    margin-right: 24rpx;
    font-size: 26rpx;
    color: #666;
    .arrow { font-size: 20rpx; margin-left: 4rpx; }
  }
  .search-entry { color: #07c160; }
}
.list-scroll { padding: 24rpx; }
.list-inner { padding-bottom: 120rpx; }
.loading-tip, .empty {
  text-align: center;
  padding: 32rpx;
  color: #999;
  font-size: 26rpx;
}
.publish-btn {
  position: fixed;
  right: 32rpx;
  bottom: 120rpx;
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background: #07c160;
  color: #fff;
  font-size: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 16rpx rgba(7, 193, 96, 0.4);
}
</style>
