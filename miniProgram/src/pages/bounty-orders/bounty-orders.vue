<template>
  <view class="page">
    <view v-if="!list.length && !loading" class="empty">暂无赏金记录</view>
    <scroll-view
      v-else
      scroll-y
      class="list-scroll"
      :style="{ height: scrollHeight + 'px' }"
      @scrolltolower="loadMore"
    >
      <view class="list-inner">
        <view v-for="o in list" :key="o.id" class="order-card">
          <view class="row">
            <text class="label">帖子 ID</text>
            <text class="link" @tap="goPost(o.postId)">{{ o.postId }}</text>
          </view>
          <view class="row">
            <text class="label">赏金</text>
            <text class="amount">¥ {{ o.amount }}</text>
          </view>
          <view class="row">
            <text class="label">状态</text>
            <text class="status">{{ statusLabel(o.status) }}</text>
          </view>
          <view class="row" v-if="o.paidAt">
            <text class="label">打款时间</text>
            <text>{{ formatTime(o.paidAt) }}</text>
          </view>
          <view class="actions" v-if="o.status === 'created'">
            <button size="mini" class="btn-escrow" @tap="doEscrow(o.id)">去托管</button>
          </view>
          <view class="actions" v-if="o.status === 'escrowed' && o.answerId">
            <button size="mini" class="btn-confirm" @tap="doConfirmPay(o.id)">确认打款</button>
          </view>
        </view>
        <view v-if="loading" class="tip">加载中...</view>
        <view v-else-if="noMore && list.length" class="tip">没有更多了</view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listMyBountyOrders, escrowBountyOrder, confirmPayBountyOrder } from '@/api/bounty.js'

const list = ref([])
const page = ref(0)
const size = 20
const loading = ref(false)
const noMore = ref(false)
const scrollHeight = ref(400)

function statusLabel(s) {
  const m = { created: '待托管', escrowed: '已托管', paid: '已打款', refunded: '已退款', disputed: '纠纷' }
  return m[s] || s
}
function formatTime(iso) {
  if (!iso) return ''
  const d = new Date(iso)
  return d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + ' ' + d.getHours() + ':' + String(d.getMinutes()).padStart(2, '0')
}

async function fetchList() {
  if (loading.value) return
  loading.value = true
  try {
    const res = await listMyBountyOrders(page.value, size)
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

function goPost(id) {
  uni.navigateTo({ url: `/pages/post-detail/post-detail?id=${id}` })
}

async function doEscrow(id) {
  try {
    await escrowBountyOrder(id)
    uni.showToast({ title: '已提交托管' })
    page.value = 0
    list.value = []
    fetchList()
  } catch (_) {}
}

async function doConfirmPay(id) {
  try {
    await confirmPayBountyOrder(id)
    uni.showToast({ title: '已确认打款' })
    page.value = 0
    list.value = []
    fetchList()
  } catch (_) {}
}

onMounted(() => {
  const sys = uni.getSystemInfoSync()
  scrollHeight.value = (sys.windowHeight || 400) - 100
  fetchList()
})
</script>

<style lang="scss" scoped>
.page { background: #f5f5f5; min-height: 100vh; }
.empty { padding: 80rpx; text-align: center; color: #999; }
.list-scroll { padding: 24rpx; }
.list-inner { padding-bottom: 48rpx; }
.order-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
  .row { display: flex; justify-content: space-between; margin-bottom: 12rpx; font-size: 28rpx; }
  .label { color: #999; }
  .link { color: #07c160; }
  .amount { font-weight: bold; color: #e54; }
  .status { color: #666; }
  .actions { margin-top: 20rpx; padding-top: 16rpx; border-top: 1rpx solid #eee; }
  .btn-escrow, .btn-confirm { background: #07c160; color: #fff; border: none; }
}
.tip { text-align: center; padding: 24rpx; color: #999; font-size: 26rpx; }
</style>
