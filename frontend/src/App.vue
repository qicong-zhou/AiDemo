<script setup>
import { ref, computed } from 'vue'

// ① 数据：会变的东西，一个一个列出来
const memos = ref([])          // 一开始是空的，等一下从后端拿
const loading = ref(true)      // 正在加载
const error = ref('')          // 出错信息
const saving = ref(false)      // 正在保存

const keyword = ref('')
const activeTag = ref(null)

const selectedId = ref(null)   // 正在看哪一条（存 id，不存整条）
const viewing = ref('empty')   // empty / detail / editor
const draft = ref({ id: null, title: '', content: '' })

// ② 工具：拆标签
function splitTags(tag) {
  return (tag || '').split(',').filter(Boolean)
}

// 补零：5 → "05"
function pad(n) {
  return String(n).padStart(2, '0')
}

// 列表用的短时间：今天只显示时:分，昨天显示"昨天"，今年显示"11月16日"
function fmtTime(iso) {
  if (!iso) return ''
  const d = new Date(iso)
  const now = new Date()

  if (d.toDateString() === now.toDateString()) return `${pad(d.getHours())}:${pad(d.getMinutes())}`

  const yesterday = new Date(now)
  yesterday.setDate(now.getDate() - 1)
  if (d.toDateString() === yesterday.toDateString()) return '昨天'

  if (d.getFullYear() === now.getFullYear()) return `${d.getMonth() + 1}月${d.getDate()}日`
  return `${d.getFullYear()}/${d.getMonth() + 1}/${d.getDate()}`
}

// 详情用的完整时间：2026/11/16 20:07
function fmtFull(iso) {
  if (!iso) return ''
  const d = new Date(iso)
  return `${d.getFullYear()}/${d.getMonth() + 1}/${d.getDate()} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

// ③ 算出来的
const allTags = computed(() => {
  const set = new Set()
  memos.value.forEach(m => splitTags(m.tag).forEach(t => set.add(t)))
  return [...set]
})

const shown = computed(() => {
  let list = memos.value
  if (activeTag.value) list = list.filter(m => splitTags(m.tag).includes(activeTag.value))
  const kw = keyword.value.trim()
  if (kw) {
    list = list.filter(
      m => (m.title || '').includes(kw) || (m.content || '').includes(kw) || (m.tag || '').includes(kw)
    )
  }
  return list
})

const current = computed(() => memos.value.find(m => m.id === selectedId.value))
const canSave = computed(() => draft.value.title.trim() || draft.value.content.trim())

// 详情里的时间文案
const currentTimeText = computed(() => {
  const m = current.value
  if (!m) return ''

  const created = fmtFull(m.createTime)
  // 更新时间比创建时间晚 1 秒以上，才算"改过"
  const changed =
    m.updateTime && m.createTime && new Date(m.updateTime) - new Date(m.createTime) > 1000

  return changed ? `创建于 ${created} · 更新于 ${fmtFull(m.updateTime)}` : `创建于 ${created}`
})

// 轻提示
const toast = ref('')
let toastTimer = null

function showToast(message) {
  toast.value = message
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => {
    toast.value = ''
  }, 2000)
}

// ④ 和后端打交道
async function loadMemos() {
  loading.value = true
  error.value = ''
  try {
    const res = await fetch('/memos')
    if (!res.ok) throw new Error(`后端返回 ${res.status}`)

    const list = await res.json()
    // 按创建时间倒序：新的在上面（后端排序不可靠，前端补一道）
    memos.value = list.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  } catch (e) {
    error.value = '加载失败：' + e.message
  } finally {
    loading.value = false
  }
}

// 页面一打开就拉一次
loadMemos()

// ⑤ 用户动作
function openDetail(id) {
  selectedId.value = id
  viewing.value = 'detail'
}

function openNew() {
  selectedId.value = null
  draft.value = { id: null, title: '', content: '' }
  viewing.value = 'editor'
}

function openEdit() {
  const m = current.value
  draft.value = { id: m.id, title: m.title || '', content: m.content || '' }
  viewing.value = 'editor'
}

function cancelEdit() {
  viewing.value = selectedId.value ? 'detail' : 'empty'
}

async function save() {
  if (!canSave.value || saving.value) return

  saving.value = true
  error.value = ''

  const isNew = draft.value.id === null
  const url = isNew ? '/memos' : `/memos/${draft.value.id}`
  const method = isNew ? 'POST' : 'PUT'
  const body = {
    title: draft.value.title.trim() || '无标题',
    content: draft.value.content.trim(),
  }

  try {
    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    })
    if (!res.ok) throw new Error(`后端返回 ${res.status}`)

    await loadMemos()
    selectedId.value = null
    viewing.value = 'empty'
    showToast(isNew ? '已新建' : '已保存')
  } catch (e) {
    error.value = '保存失败：' + e.message
  } finally {
    saving.value = false
  }
}

async function remove() {
  if (!confirm('确定删除这条备忘录？')) return

  error.value = ''
  try {
    const res = await fetch(`/memos/${selectedId.value}`, { method: 'DELETE' })
    if (!res.ok) throw new Error(`后端返回 ${res.status}`)

    selectedId.value = null
    viewing.value = 'empty'
    await loadMemos()
    showToast('已删除')
  } catch (e) {
    error.value = '删除失败：' + e.message
  }
}
</script>

<template>
  <div class="app">
    <!-- 左栏 -->
    <aside class="side">
      <h1>我的备忘录</h1>

      <button class="new-btn" @click="openNew">+ 新建备忘录</button>

      <input v-model="keyword" placeholder="搜索" />

      <div class="tagbar">
        <button class="chip" :class="{ active: activeTag === null }" @click="activeTag = null">全部</button>
        <button
          v-for="t in allTags"
          :key="t"
          class="chip"
          :class="{ active: activeTag === t }"
          @click="activeTag = t"
        >
          {{ t }}
        </button>
      </div>

      <p v-if="error" class="error">{{ error }}</p>

      <p class="count">
        显示 {{ shown.length }} 条 / 共 {{ memos.length }} 条
      </p>

      <div class="list">
        <article
          v-for="m in shown"
          :key="m.id"
          class="memo"
          :class="{ picked: selectedId === m.id }"
          @click="openDetail(m.id)"
        >
          <div class="memo-top">
            <h3>{{ m.title }}</h3>
            <time>{{ fmtTime(m.createTime) }}</time>
          </div>
          <p>{{ m.content }}</p>
        </article>

        <!-- 首次加载时先放三个灰块，别让页面空着 -->
        <div v-if="loading && memos.length === 0" class="skeleton">
          <div v-for="i in 3" :key="i" class="sk-item"></div>
        </div>
      </div>

      <p v-if="!loading && memos.length === 0" class="empty">
        还没有备忘录，点上面的按钮写一条
      </p>
      <p v-else-if="!loading && shown.length === 0" class="empty">
        没有匹配的条目，换个词或取消标签筛选
      </p>
    </aside>

    <!-- 右栏：三种样子，靠 viewing 一个变量切换 -->
    <main class="main">
      <div v-if="viewing === 'empty'" class="placeholder">
        <p>左边选一条看，或者新建一条</p>
      </div>

      <article v-else-if="viewing === 'detail' && current" class="detail">
        <h2>{{ current.title }}</h2>
        <p class="meta">{{ currentTimeText }}</p>
        <div class="acts">
          <button @click="openEdit">编辑</button>
          <button class="danger" @click="remove">删除</button>
        </div>
        <p class="body">{{ current.content }}</p>
        <p class="tags">
          <span v-for="t in splitTags(current.tag)" :key="t" class="tag">{{ t }}</span>
        </p>
      </article>

      <div v-else-if="viewing === 'editor'" class="editor">
        <input v-model="draft.title" placeholder="标题" class="title-input" />
        <textarea
          v-model="draft.content"
          placeholder="写点什么…（Ctrl + Enter 保存，Esc 取消）"
          @keydown.ctrl.enter="save"
          @keydown.esc="cancelEdit"
        ></textarea>
        <div class="acts">
          <button :disabled="!canSave || saving" @click="save">
            {{ saving ? 'AI 打标签中…' : '保存' }}
          </button>
          <button :disabled="saving" @click="cancelEdit">取消</button>
        </div>
      </div>
    </main>

    <!-- 轻提示：有字才出现 -->
    <div v-if="toast" class="toast">{{ toast }}</div>
  </div>
</template>

<style scoped>
.app {
  display: flex;
  min-height: 100vh;
}

.side {
  width: 320px;
  flex-shrink: 0;
  padding: 24px 20px;
  border-right: 1px solid #e5e3db;
  display: flex;
  flex-direction: column;
}

h1 {
  margin: 0 0 14px;
  font-size: 18px;
}

.new-btn {
  height: 34px;
  margin-bottom: 12px;
  border: none;
  border-radius: 8px;
  background: #2f5f4f;
  color: #fff;
  font-size: 13px;
  font-family: inherit;
  cursor: pointer;
}

input,
textarea {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 13px;
  font-family: inherit;
  outline: none;
}

input:focus,
textarea:focus {
  border-color: #2f5f4f;
}

.tagbar {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 10px;
}

.chip {
  padding: 3px 10px;
  border: 1px solid #ddd;
  border-radius: 999px;
  background: #fff;
  color: #5c615b;
  font-size: 12px;
  font-family: inherit;
  cursor: pointer;
}

.chip.active {
  background: #2f5f4f;
  border-color: #2f5f4f;
  color: #fff;
}

.count {
  margin: 10px 0;
  font-size: 12px;
  color: #8d918a;
}

.list {
  flex: 1;
  overflow-y: auto;
}

.memo {
  padding: 12px 14px;
  margin-bottom: 8px;
  background: #fff;
  border: 1px solid #e5e3db;
  border-radius: 10px;
  cursor: pointer;
}

.memo.picked {
  border-color: #2f5f4f;
  background: #f4f8f5;
}

.memo-top {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 8px;
}

.memo-top time {
  flex-shrink: 0;
  font-size: 11px;
  color: #a8aca4;
}

.memo h3 {
  margin: 0 0 5px;
  font-size: 13.5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.memo p {
  margin: 0;
  font-size: 12px;
  color: #7c8079;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 骨架屏：加载时占位 */
.sk-item {
  height: 64px;
  margin-bottom: 8px;
  border-radius: 10px;
  background: linear-gradient(90deg, #f2f1ec 25%, #e8e7e0 37%, #f2f1ec 63%);
  background-size: 400% 100%;
  animation: sk 1.4s ease infinite;
}

@keyframes sk {
  0% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0 50%;
  }
}

/* 轻提示 */
.toast {
  position: fixed;
  left: 50%;
  bottom: 40px;
  transform: translateX(-50%);
  padding: 8px 18px;
  border-radius: 999px;
  background: rgba(40, 44, 40, 0.88);
  color: #fff;
  font-size: 13px;
  pointer-events: none;
}

.main {
  flex: 1;
  padding: 40px;
}

.placeholder,
.empty {
  color: #8d918a;
  font-size: 13px;
}

.placeholder {
  padding-top: 60px;
  text-align: center;
}

.error {
  margin: 8px 0;
  padding: 6px 10px;
  border-radius: 6px;
  background: #fdecea;
  color: #b4433c;
  font-size: 12px;
  line-height: 1.5;
}

.detail h2 {
  margin: 0 0 6px;
  font-size: 20px;
}

.detail .meta {
  margin: 0 0 18px;
  font-size: 11.5px;
  color: #a8aca4;
}

.detail .body {
  font-size: 14px;
  line-height: 1.8;
  color: #3d423c;
  white-space: pre-wrap;
}

.acts {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.acts button {
  padding: 6px 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  font-size: 13px;
  font-family: inherit;
  cursor: pointer;
}

.acts button:disabled {
  color: #c0c4be;
  cursor: not-allowed;
}

.acts button.danger {
  color: #b4433c;
  border-color: #eccfcc;
}

.editor {
  max-width: 560px;
}

.editor .title-input {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 12px;
}

.editor textarea {
  height: 260px;
  resize: vertical;
  line-height: 1.7;
}

.tags {
  display: flex;
  gap: 6px;
  margin-top: 16px;
}

.tag {
  padding: 1px 8px;
  border-radius: 999px;
  background: #e9f1ec;
  color: #2f5f4f;
  font-size: 11px;
}
</style>
