<template>
  <view class="page">
    <view v-if="loading && !post" class="loading-wrap">加载中...</view>
    <view v-else-if="post" class="detail">
      <view class="card">
        <view class="type-era">
          <text class="type-tag">{{ typeLabel }}</text>
          <text v-if="post.sceneEra" class="era-tag">{{ eraLabel }}</text>
          <text v-if="post.bountyMode && post.bountyMode !== 'none'" class="bounty-tag">赏金 {{ post.bountyAmount ? post.bountyAmount + '元' : '' }}</text>
        </view>
        <view v-if="post.coverImage" class="cover-wrap">
          <image class="cover" :src="post.coverImage" mode="widthFix" />
        </view>
        <view v-if="post.sketchImage" class="sketch-wrap">
          <image class="sketch" :src="post.sketchImage" mode="widthFix" />
        </view>
        <text class="desc">{{ post.description }}</text>
        <view class="tags" v-if="post.tags && post.tags.length">
          <text v-for="t in post.tags" :key="t.id" class="tag">{{ t.name }}</text>
        </view>
        <view class="meta">
          <text>{{ post.answerCount || 0 }} 回答</text>
          <text class="time">{{ timeStr }}</text>
        </view>
        <view class="actions">
          <button v-if="!isOwner" size="mini" class="btn-fav" @tap="toggleFavorite">
            {{ isFavorited ? '已收藏' : '收藏' }}
          </button>
          <template v-if="isOwner">
            <button size="mini" @tap="goEdit">编辑</button>
            <button
              v-if="post.bountyMode && post.bountyMode !== 'none' && post.bountyAmount"
              size="mini"
              class="btn-bounty"
              @tap="goBountyOrders"
            >
              赏金记录
            </button>
          </template>
        </view>
      </view>

      <view class="section-title">回答 ({{ answers.length }})</view>
      <view class="answer-list">
        <answer-item
          v-for="a in answers"
          :key="a.id"
          :answer="a"
          :show-accept="isOwner && post.status === 'open'"
          @accept="acceptAnswer(a.id)"
        />
      </view>
      <view v-if="answersLoading" class="loading-tip">加载中...</view>
      <view v-else-if="answersNoMore && answers.length === 0" class="empty-tip">暂无回答，来写第一条吧</view>

      <view v-if="post.status === 'open' && !isOwner" class="reply-form">
        <view class="form-title">写下你的回答</view>
        <textarea
          v-model="replyForm.contentPublic"
          placeholder="公开内容：名称、特征等（必填）"
          class="textarea"
          maxlength="500"
        />
        <textarea
          v-model="replyForm.contentPrivate"
          placeholder="仅求助者可见：购买链接、渠道等（选填）"
          class="textarea small"
          maxlength="300"
        />
        <button class="submit-btn" @tap="submitAnswer">提交回答</button>
      </view>
    </view>
    <view v-else class="empty-tip">帖子不存在</view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getPost } from '@/api/post.js'
import { listAnswers, createAnswer, acceptAnswer as apiAcceptAnswer } from '@/api/answer.js'
import { addFavorite, removeFavorite } from '@/api/favorite.js'
import { useUserStore } from '@/store/user.js'

const postId = ref(0)
const post = ref(null)
const loading = ref(true)
const answers = ref([])
const answersPage = ref(0)
const answersLoading = ref(false)
const answersNoMore = ref(false)
const replyForm = ref({ contentPublic: '', contentPrivate: '' })
const isFavorited = ref(false)

const userStore = useUserStore()

const typeMap = { toy: '玩具', stationery: '文具', snack: '零食', old_object: '老物件' }
const eraMap = { '80s': '80后', '90s': '90后', '00s': '00后' }
const typeLabel = computed(() => typeMap[post.value?.type] || post.value?.type || '')
const eraLabel = computed(() => eraMap[post.value?.sceneEra] || post.value?.sceneEra || '')
const timeStr = computed(() => {
  const iso = post.value?.createdAt
  if (!iso) return ''
  const d = new Date(iso)
  return d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate()
})
const isOwner = computed(() => userStore.user && post.value && userStore.user.id === post.value.userId)

onLoad((q) => {
  postId.value = Number(q?.id) || 0
})

async function loadPost() {
  if (!postId.value) return
  loading.value = true
  try {
    post.value = await getPost(postId.value)
  } catch (_) {}
  finally {
    loading.value = false
  }
}

async function loadAnswers() {
  if (!postId.value) return
  answersLoading.value = true
  try {
    const res = await listAnswers(postId.value, answersPage.value, 20)
    const content = Array.isArray(res) ? res : (res.content || [])
    if (answersPage.value === 0) answers.value = content
    else answers.value = answers.value.concat(content)
    answersNoMore.value = Array.isArray(res) ? true : (res.last !== false && content.length < 20)
  } catch (_) {}
  finally {
    answersLoading.value = false
  }
}

onMounted(() => {
  loadPost()
})

watch(post, (p) => {
  if (p) {
    answersPage.value = 0
    answers.value = []
    loadAnswers()
  }
}, { immediate: true })

function goEdit() {
  uni.navigateTo({ url: `/pages/post-publish/post-publish?id=${postId.value}` })
}

function goBountyOrders() {
  uni.navigateTo({ url: '/pages/bounty-orders/bounty-orders' })
}

async function toggleFavorite() {
  if (!userStore.isLoggedIn) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    return
  }
  try {
    if (isFavorited.value) {
      await removeFavorite(postId.value)
      isFavorited.value = false
      uni.showToast({ title: '已取消收藏' })
    } else {
      await addFavorite(postId.value)
      isFavorited.value = true
      uni.showToast({ title: '已收藏' })
    }
  } catch (_) {}
}

async function submitAnswer() {
  const { contentPublic } = replyForm.value
  if (!contentPublic?.trim()) {
    uni.showToast({ title: '请填写公开内容', icon: 'none' })
    return
  }
  if (!userStore.isLoggedIn) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    return
  }
  try {
    await createAnswer(postId.value, {
      postId: postId.value,
      contentPublic: contentPublic.trim(),
      contentPrivate: replyForm.value.contentPrivate?.trim() || undefined,
    })
    uni.showToast({ title: '提交成功' })
    replyForm.value = { contentPublic: '', contentPrivate: '' }
    answersPage.value = 0
    answers.value = []
    loadAnswers()
    if (post.value) post.value.answerCount = (post.value.answerCount || 0) + 1
  } catch (_) {}
}

async function acceptAnswer(answerId) {
  try {
    await apiAcceptAnswer(postId.value, answerId)
    uni.showToast({ title: '已采纳' })
    loadPost()
    loadAnswers()
  } catch (_) {}
}
</script>

<style lang="scss" scoped>
.page { padding: 24rpx; padding-bottom: 80rpx; background: #f5f5f5; min-height: 100vh; }
.loading-wrap { padding: 80rpx; text-align: center; color: #999; }
.detail .card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
}
.type-era {
  margin-bottom: 16rpx;
  .type-tag, .era-tag, .bounty-tag {
    font-size: 22rpx;
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
    margin-right: 12rpx;
  }
  .type-tag { background: #e8f4fd; color: #07c160; }
  .era-tag { background: #fff3e0; color: #f90; }
  .bounty-tag { background: #ffe8e8; color: #e54; }
}
.cover-wrap, .sketch-wrap { margin-bottom: 16rpx; border-radius: 12rpx; overflow: hidden; }
.cover, .sketch { width: 100%; display: block; }
.desc { font-size: 28rpx; color: #333; line-height: 1.6; display: block; }
.tags { margin-top: 16rpx; .tag { font-size: 24rpx; color: #999; margin-right: 16rpx; } }
.meta { margin-top: 20rpx; font-size: 24rpx; color: #999; .time { margin-left: 24rpx; } }
.actions { margin-top: 20rpx; display: flex; gap: 16rpx; flex-wrap: wrap; }
.btn-bounty { background: #ffe8e8; color: #e54; border: none; }
.btn-fav { background: #f0f0f0; color: #666; border: none; }
.section-title { font-size: 30rpx; font-weight: bold; margin-bottom: 16rpx; }
.answer-list { margin-bottom: 24rpx; }
.loading-tip, .empty-tip { text-align: center; padding: 24rpx; color: #999; font-size: 26rpx; }
.reply-form {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  .form-title { font-size: 28rpx; margin-bottom: 16rpx; }
  .textarea { width: 100%; min-height: 160rpx; padding: 16rpx; border: 1rpx solid #eee; border-radius: 12rpx; margin-bottom: 16rpx; box-sizing: border-box; }
  .textarea.small { min-height: 100rpx; }
  .submit-btn { background: #07c160; color: #fff; border: none; border-radius: 12rpx; height: 80rpx; line-height: 80rpx; }
}
</style>
