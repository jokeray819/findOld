<template>
  <view class="page">
    <view class="search-bar">
      <input
        v-model="keyword"
        class="search-input"
        placeholder="输入关键词，如：红色塑料陀螺 80后"
        confirm-type="search"
        @confirm="onSearch"
      />
      <view class="search-btn" @tap="onSearch">搜索</view>
    </view>
    <view class="filter-row">
      <picker class="filter-item" mode="selector" :range="typeOptions" range-key="label" @change="onTypeChange">
        <text>{{ typeLabel }}</text>
      </picker>
      <picker class="filter-item" mode="selector" :range="eraOptions" range-key="label" @change="onEraChange">
        <text>{{ eraLabel }}</text>
      </picker>
    </view>
    <scroll-view
      scroll-y
      class="list-scroll"
      :style="{ height: scrollHeight + 'px' }"
      @scrolltolower="loadMore"
    >
      <view class="list-inner">
        <post-card v-for="p in list" :key="p.id" :post="p" />
        <view v-if="loading" class="tip">加载中...</view>
        <view v-else-if="noMore && list.length" class="tip">没有更多了</view>
        <view v-else-if="searched && !loading && !list.length" class="tip">无结果</view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { searchPosts } from '@/api/post.js'

const keyword = ref('')
const list = ref([])
const page = ref(0)
const size = 20
const loading = ref(false)
const noMore = ref(false)
const searched = ref(false)
const scrollHeight = ref(400)

const filter = ref({ type: '', sceneEra: '' })
const typeOptions = [
  { value: '', label: '全部类型' },
  { value: 'toy', label: '玩具' },
  { value: 'stationery', label: '文具' },
  { value: 'snack', label: '零食' },
  { value: 'old_object', label: '老物件' },
]
const eraOptions = [
  { value: '', label: '全部年代' },
  { value: '80s', label: '80后' },
  { value: '90s', label: '90后' },
  { value: '00s', label: '00后' },
]
const typeLabel = computed(() => typeOptions.find((x) => x.value === filter.value.type)?.label || '类型')
const eraLabel = computed(() => eraOptions.find((x) => x.value === filter.value.sceneEra)?.label || '年代')

function onTypeChange(e) {
  filter.value.type = typeOptions[Number(e.detail?.value ?? 0)].value
  reload()
}
function onEraChange(e) {
  filter.value.sceneEra = eraOptions[Number(e.detail?.value ?? 0)].value
  reload()
}

function onSearch() {
  searched.value = true
  reload()
}

function reload() {
  page.value = 0
  noMore.value = false
  list.value = []
  fetchList()
}

async function fetchList() {
  if (loading.value) return
  loading.value = true
  try {
    const res = await searchPosts({
      q: keyword.value?.trim() || undefined,
      type: filter.value.type || undefined,
      sceneEra: filter.value.sceneEra || undefined,
      page: page.value,
      size,
    })
    const content = res.content || []
    if (page.value === 0) list.value = content
    else list.value = list.value.concat(content)
    noMore.value = res.last !== undefined ? res.last : list.value.length >= (res.totalElements ?? 0)
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

onMounted(() => {
  const sys = uni.getSystemInfoSync()
  scrollHeight.value = (sys.windowHeight || 400) - 200
})
</script>

<style lang="scss" scoped>
.page { background: #f5f5f5; min-height: 100vh; }
.search-bar {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: #fff;
  .search-input { flex: 1; height: 72rpx; padding: 0 24rpx; background: #f5f5f5; border-radius: 12rpx; font-size: 28rpx; }
  .search-btn { margin-left: 24rpx; color: #07c160; font-size: 28rpx; }
}
.filter-row {
  display: flex;
  padding: 16rpx 24rpx;
  background: #fff;
  border-top: 1rpx solid #eee;
  .filter-item { margin-right: 32rpx; font-size: 26rpx; color: #666; }
}
.list-scroll { padding: 24rpx; }
.list-inner { padding-bottom: 48rpx; }
.tip { text-align: center; padding: 32rpx; color: #999; font-size: 26rpx; }
</style>
