<template>
  <div class="student-help">
    <!-- 头部 -->
    <div class="help-header">
      <h1>帮助与支持</h1>
      <p>如有其他问题，请联系管理员</p>
    </div>

    <!-- 搜索和分类筛选 -->
    <div class="filter-section">
      <!-- <div class="search-box">
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="输入关键词搜索问题或文档..."
          @input="handleSearch"
        />
        <button @click="handleSearch">搜索</button>
      </div> -->
      <!-- <div class="category-filter">
        <label>分类筛选：</label>
        <select v-model="selectedCategory" @change="handleCategoryChange">
          <option value="">全部</option>
          <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
        </select>
      </div> -->
    </div>

    <!-- 标签页切换 -->
    <div class="tab-section">
      <button
        :class="{ active: activeTab === 'faqs' }"
        @click="activeTab = 'faqs'"
      >
        常见问题
      </button>
      <button
        :class="{ active: activeTab === 'documents' }"
        @click="activeTab = 'documents'"
      >
        帮助文档
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">加载中...</div>

    <!-- 常见问题列表 -->
    <div v-if="activeTab === 'faqs' && !loading" class="faq-list">
      <div v-if="faqs.length === 0" class="empty">暂无常见问题</div>
      <div v-else class="faq-item" v-for="faq in faqs" :key="faq.faqId">
        <div class="faq-question" @click="toggleFaq(faq.faqId)">
          <strong>{{ faq.question }}</strong>
          <span class="toggle-icon">{{ expandedFaq === faq.faqId ? '−' : '+' }}</span>
        </div>
        <div v-if="expandedFaq === faq.faqId" class="faq-answer">
          {{ faq.answer }}
          <div class="faq-category">分类：{{ faq.category }}</div>
        </div>
      </div>
    </div>

    <!-- 帮助文档列表 -->
    <div v-if="activeTab === 'documents' && !loading" class="document-list">
      <div v-if="documents.length === 0" class="empty">暂无帮助文档</div>
      <div v-else class="document-item" v-for="doc in documents" :key="doc.documentId" @click="viewDocument(doc.documentId)">
        <div class="doc-title">
          {{ doc.title }}
        </div>
        <div class="doc-meta">
          <span class="doc-category">{{ doc.category }}</span>
          <span class="doc-date">{{ formatDate(doc.createdAt) }}</span>
        </div>
        <div class="doc-summary" v-if="doc.summary">
          {{ formatSummary(doc.summary) }}
        </div>
      </div>
    </div>

    <!-- 文档详情模态框 -->
    <div v-if="showDocumentModal" class="modal-overlay" @click="closeDocumentModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ currentDocument.title }}</h3>
          <button class="close-btn" @click="closeDocumentModal">×</button>
        </div>
        <div class="modal-body">
          <div class="doc-meta">
            <span>分类：{{ currentDocument.category }}</span>
            <span>发布时间：{{ formatDate(currentDocument.createdAt) }}</span>
          </div>
          <div class="doc-content" v-html="formatContent(currentDocument.content || currentDocument.summary)"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { supportApi } from '@/api/studentApi.js'

// 响应式数据
const activeTab = ref('faqs')
const searchKeyword = ref('')
const selectedCategory = ref('')
const categories = ref([])
const faqs = ref([])
const documents = ref([])
const expandedFaq = ref(null)
const currentDocument = ref({})
const showDocumentModal = ref(false)
const loading = ref(false)

// 从FAQ中提取所有分类
const extractCategoriesFromFaqs = (faqs) => {
  const cats = new Set()
  faqs.forEach(faq => {
    if (faq.category) cats.add(faq.category)
  })
  return Array.from(cats)
}

// 获取常见问题
const fetchFaqs = async () => {
  loading.value = true
  try {
    const params = {}
    if (selectedCategory.value) params.category = selectedCategory.value
    if (searchKeyword.value) params.keyword = searchKeyword.value
    const res = await supportApi.getFaqs(params)
    console.log('获取到的常见问题数据:', res)
    
    // 根据API数据结构调整
    faqs.value = Array.isArray(res) ? res : (res.list || [])
    
    // 动态获取分类
    if (faqs.value.length > 0) {
      const extractedCats = extractCategoriesFromFaqs(faqs.value)
      if (extractedCats.length > 0) {
        categories.value = extractedCats
      }
    }
  } catch (error) {
    console.error('获取常见问题失败:', error)
    alert('获取常见问题失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 获取帮助文档
const fetchDocuments = async () => {
  loading.value = true
  try {
    const params = {}
    if (selectedCategory.value) params.category = selectedCategory.value
    if (searchKeyword.value) params.keyword = searchKeyword.value
    const res = await supportApi.getDocuments(params)
    console.log('获取到的帮助文档数据:', res)
    
    // 根据API数据结构调整
    documents.value = Array.isArray(res) ? res : (res.list || [])
    
    // 从文档中提取分类（如果没有从FAQ中提取到）
    if (categories.value.length === 0 && documents.value.length > 0) {
      const docCats = new Set()
      documents.value.forEach(doc => {
        if (doc.category) docCats.add(doc.category)
      })
      categories.value = Array.from(docCats)
    }
  } catch (error) {
    console.error('获取帮助文档失败:', error)
    alert('获取帮助文档失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 搜索处理
// const handleSearch = () => {
//   if (activeTab.value === 'faqs') {
//     fetchFaqs()
//   } else {
//     fetchDocuments()
//   }
// }

// // 分类筛选处理
// const handleCategoryChange = () => {
//   handleSearch()
// }

// 切换FAQ展开/收起
const toggleFaq = (faqId) => {
  expandedFaq.value = expandedFaq.value === faqId ? null : faqId
}

// 查看文档详情
const viewDocument = async (documentId) => {
  try {
    const res = await supportApi.getDocumentDetail(documentId)
    console.log('获取到的文档详情:', res)
    currentDocument.value = res
    showDocumentModal.value = true
  } catch (error) {
    console.error('获取文档详情失败:', error)
    alert('获取文档详情失败，请稍后重试')
  }
}

// 关闭文档详情模态框
const closeDocumentModal = () => {
  showDocumentModal.value = false
  currentDocument.value = {}
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  try {
    // 处理ISO格式日期
    const date = new Date(dateStr)
    return date.toLocaleDateString('zh-CN')
  } catch (e) {
    // 如果解析失败，返回原始字符串或截取部分
    return dateStr.split('T')[0] || dateStr
  }
}

// 格式化摘要（限制长度）
const formatSummary = (summary) => {
  if (!summary) return ''
  const maxLength = 100
  if (summary.length > maxLength) {
    return summary.substring(0, maxLength) + '...'
  }
  return summary
}

// 格式化内容（将换行转换为<br>）
const formatContent = (content) => {
  if (!content) return ''
  return content.replace(/\n/g, '<br>')
}

// 监听标签页切换
onMounted(() => {
  fetchFaqs()
})

// 当标签页切换时重新加载对应数据
watch(activeTab, (newTab) => {
  if (newTab === 'faqs') {
    fetchFaqs()
  } else {
    fetchDocuments()
  }
})
</script>

<style scoped>
.student-help {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.help-header {
  text-align: center;
  margin-bottom: 30px;
}

.help-header h1 {
  font-size: 2rem;
  margin-bottom: 10px;
}

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.search-box {
  display: flex;
  gap: 10px;
}

.search-box input {
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  width: 300px;
}

.search-box button {
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.category-filter select {
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.tab-section {
  display: flex;
  border-bottom: 2px solid #eee;
  margin-bottom: 20px;
}

.tab-section button {
  padding: 10px 20px;
  background: none;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  border-bottom: 3px solid transparent;
}

.tab-section button.active {
  border-bottom-color: #007bff;
  font-weight: bold;
  color: #007bff;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #666;
}

.faq-list,
.document-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.faq-item,
.document-item {
  border: 1px solid #eee;
  border-radius: 6px;
  padding: 15px;
  background: white;
  cursor: pointer;
  transition: all 0.2s ease;
}

.faq-item:hover,
.document-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.faq-question {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toggle-icon {
  font-size: 1.2rem;
  font-weight: bold;
  color: #007bff;
}

.faq-answer {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed #ddd;
  color: #555;
}

.faq-category {
  margin-top: 8px;
  font-size: 0.9rem;
  color: #888;
  font-style: italic;
}

.doc-title {
  font-size: 1.1rem;
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.doc-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.9rem;
  color: #777;
  margin-bottom: 8px;
}

.doc-summary {
  font-size: 0.9rem;
  color: #666;
  line-height: 1.4;
  padding: 8px 0;
  border-top: 1px solid #f0f0f0;
  margin-top: 8px;
}

.empty {
  text-align: center;
  padding: 40px;
  color: #999;
  font-style: italic;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  background: #f8f9fa;
}

.modal-header h3 {
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #666;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
  overflow-y: auto;
}

.modal-body .doc-meta {
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  margin-bottom: 15px;
}

.doc-content {
  line-height: 1.8;
  color: #444;
}

.doc-content br {
  margin-bottom: 8px;
}
</style>