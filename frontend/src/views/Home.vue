<template>
  <div class="home-container">
    <el-header class="header">
      <div class="header-content">
        <h1>在线订餐系统</h1>
        <div class="header-actions">
          <template v-if="!userStore.isLoggedIn()">
            <el-button type="primary" @click="$router.push('/login')">登录</el-button>
          </template>
          <template v-else>
            <el-badge :value="cartCount" class="cart-badge" v-if="!userStore.isAdmin()">
              <el-button @click="$router.push('/cart')">购物车</el-button>
            </el-badge>
            <el-dropdown @command="handleCommand">
              <el-button>
                {{ userStore.username }} <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="center" v-if="!userStore.isAdmin()">个人中心</el-dropdown-item>
                  <el-dropdown-item command="dishes" v-if="userStore.isAdmin()">菜品管理</el-dropdown-item>
                  <el-dropdown-item command="orders" v-if="userStore.isAdmin()">订单管理</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </div>
    </el-header>

    <el-main class="main-content">
      <h2 class="section-title">菜品列表</h2>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="dish in dishes" :key="dish.id">
          <el-card class="dish-card" shadow="hover">
            <div class="dish-info">
              <h3 class="dish-name">{{ dish.dishName }}</h3>
              <p class="dish-price">¥{{ dish.price }}</p>
              <p class="dish-desc">{{ dish.description }}</p>
            </div>
            <div class="dish-actions" v-if="userStore.isLoggedIn() && !userStore.isAdmin()">
              <el-button
                class="fav-btn"
                :type="favoriteIds.has(dish.id) ? 'warning' : 'default'"
                :icon="favoriteIds.has(dish.id) ? StarFilled : Star"
                circle
                @click="toggleFavorite(dish)"
              />
              <el-input-number v-model="quantities[dish.id]" :min="1" :max="99" size="small" />
              <el-button type="primary" size="small" @click="addToCart(dish)">加入购物车</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { dishApi, favoriteApi } from '../api'
import { ElMessage } from 'element-plus'
import { ArrowDown, Star, StarFilled } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const dishes = ref([])
const quantities = reactive({})
const favoriteIds = ref(new Set())
const cart = ref(JSON.parse(localStorage.getItem('cart') || '[]'))
const cartCount = ref(cart.value.reduce((sum, item) => sum + item.quantity, 0))

const loadDishes = async () => {
  try {
    const res = await dishApi.getList()
    dishes.value = res.data
    dishes.value.forEach(dish => {
      quantities[dish.id] = 1
    })
  } catch (error) {
    ElMessage.error('加载菜品失败')
  }
}

const loadFavorites = async () => {
  if (!userStore.isLoggedIn() || userStore.isAdmin()) return
  try {
    const res = await favoriteApi.getIds()
    favoriteIds.value = new Set(res.data)
  } catch (error) {
    ElMessage.error('加载收藏失败')
  }
}

const toggleFavorite = async (dish) => {
  try {
    if (favoriteIds.value.has(dish.id)) {
      await favoriteApi.remove(dish.id)
      favoriteIds.value.delete(dish.id)
      ElMessage.success('已取消收藏')
    } else {
      await favoriteApi.add(dish.id)
      favoriteIds.value.add(dish.id)
      ElMessage.success('收藏成功')
    }
    favoriteIds.value = new Set(favoriteIds.value)
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const addToCart = (dish) => {
  const quantity = quantities[dish.id]
  const existingItem = cart.value.find(item => item.dishId === dish.id)

  if (existingItem) {
    existingItem.quantity += quantity
  } else {
    cart.value.push({
      dishId: dish.id,
      dishName: dish.dishName,
      price: dish.price,
      quantity: quantity
    })
  }

  localStorage.setItem('cart', JSON.stringify(cart.value))
  cartCount.value = cart.value.reduce((sum, item) => sum + item.quantity, 0)
  ElMessage.success('已加入购物车')
}

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    localStorage.removeItem('cart')
    router.push('/login')
    ElMessage.success('已退出登录')
  } else if (command === 'center') {
    router.push('/user/center')
  } else if (command === 'dishes') {
    router.push('/admin/dishes')
  } else if (command === 'orders') {
    router.push('/admin/orders')
  }
}

onMounted(() => {
  loadDishes()
  loadFavorites()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.header {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.header-content h1 {
  margin: 0;
  color: #667eea;
  font-size: 24px;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.cart-badge {
  margin-right: 12px;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.section-title {
  color: white;
  text-align: center;
  margin-bottom: 30px;
  font-size: 32px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.dish-card {
  margin-bottom: 20px;
  border-radius: 12px;
  transition: transform 0.3s;
}

.dish-card:hover {
  transform: translateY(-5px);
}

.dish-info {
  margin-bottom: 16px;
}

.dish-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
}

.dish-price {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}

.dish-desc {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.dish-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #eee;
}

.fav-btn {
  flex-shrink: 0;
}
</style>
