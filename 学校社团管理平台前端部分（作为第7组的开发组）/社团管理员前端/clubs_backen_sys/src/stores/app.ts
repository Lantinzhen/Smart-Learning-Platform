import {defineStore} from 'pinia';
import {ref, computed} from 'vue';

// 定义主题
type Theme = 'light' | 'dark';

// 定义语言
type Language = 'zh-CN' | 'en-US';

// 定义应用程序store
export const useAppStore = defineStore('app',()=>{
   // 状态
  const theme = ref<Theme>('light');
  const language = ref<Language>('zh-CN');
  const sidebarCollapsed = ref(false);
  const loading = ref(false);


  // 切换主题
  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light';
    // 设置主题
    document.documentElement.setAttribute('data-theme', theme.value);
    // 存储主题
     localStorage.setItem('theme', theme.value);
  }

  // 设置主题
  const setTheme = (newTheme: Theme) => {
    theme.value = newTheme;
    document.documentElement.setAttribute('data-theme', newTheme);
    localStorage.setItem('theme', newTheme);
  }
  // 切换语言
  const toggleLanguage = () => { 
    language.value = language.value === 'zh-CN' ? 'en-US' : 'zh-CN';
    localStorage.setItem('language', language.value);
    
  }

  // 设置语言
  const setLanguage = (newLanguage: Language) => {
    language.value = newLanguage;
    localStorage.setItem('language', newLanguage);
  }

  // 切换侧边栏
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value;
  }

  // 设置侧边栏状态
  const setSidebarCollapsed = (collapsed: boolean) => {
    sidebarCollapsed.value = collapsed;
  }

    // 设置加载状态
  const setLoading = (isLoading: boolean) => {
    loading.value = isLoading;
  }

  // 初始化
  const init = () => {
    // 从localStorage获取主题
    const savedTheme = localStorage.getItem('theme') as Theme;
    if (savedTheme) {
      setTheme(savedTheme);
    }

    // 从localStorage获取语言
    const savedLanguage = localStorage.getItem('language') as Language;
    if (savedLanguage) {
      setLanguage(savedLanguage);
    }
  }

   return {
    theme,
    language,
    sidebarCollapsed,
    loading,
    toggleTheme,
    toggleLanguage,
    setTheme,
    setLanguage,
    toggleSidebar,
    setSidebarCollapsed,
    setLoading,
    init,
  }
});
