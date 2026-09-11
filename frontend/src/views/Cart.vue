<template>
  <div class="cart-container">
    <el-header class="header">
      <div class="header-content">
        <h1>购物车</h1>
        <el-button @click="$router.push('/')">返回首页</el-button>
      </div>
    </el-header>

    <el-main class="main-content">
      <el-card class="cart-card">
        <template v-if="cartItems.length > 0">
          <el-table :data="cartItems" stripe style="width: 100%">
            <el-table-column prop="dishName" label="菜名" />
            <el-table-column prop="price" label="单价" width="120">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column label="数量" width="180">
              <template #default="{ row }">
                <el-input-number v-model="row.quantity" :min="1" :max="99" size="small" @change="updateCart" />
              </template>
            </el-table-column>
            <el-table-column label="小计" width="120">
              <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button size="small" type="danger" @click="removeItem(row.dishId)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="cart-summary">
            <div class="summary-item">
              <span class="label">总计：</span>
              <span class="total-price">¥{{ totalPrice }}</span>
            </div>
            <div class="summary-item">
              <span class="label">当前余额：</span>
              <span class="balance">¥{{ userStore.balance }}</span>
            </div>
            <el-button type="primary" size="large" @click="handleCheckout" :loading="loading" style="width: 200px">
              结算
            </el-button>
          </div>
        </template>

        <el-empty v-else description="购物车是空的" />
      </el-card>
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { orderApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const cartItems = ref([])
const loading = ref(false)

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0).toFixed(2)
})

const loadCart = () => {
  const cart = JSON.parse(localStorage.getItem('cart') || '[]')
  cartItems.value = cart
}

const updateCart = () => {
  localStorage.setItem('cart', JSON.stringify(cartItems.value))
}

const removeItem = (dishId) => {
  cartItems.value = cartItems.value.filter(item => item.dishId !== dishId)
  updateCart()
  ElMessage.success('已删除')
}

const handleCheckout = async () => {
  if (parseFloat(totalPrice.value) > parseFloat(userStore.balance)) {
    ElMessage.error('余额不足，请先充值')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要支付 ¥${totalPrice.value} 吗？`, '确认支付', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    loading.value = true

    const orderData = {
      items: cartItems.value.map(item => ({
        dishId: item.dishId,
        quantity: item.quantity
      }))
    }

    await orderApi.create(orderData)

    // 清空购物车
    cartItems.value = []
    localStorage.removeItem('cart')

    // 更新余额
    const newBalance = (parseFloat(userStore.balance) - parseFloat(totalPrice.value)).toFixed(2)
    userStore.updateBalance(newBalance)

    ElMessage.success('下单成功')
    router.push('/user/center')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '下单失败')
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.cart-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: white;
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
  color: #333;
  font-size: 24px;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.cart-card {
  border-radius: 12px;
}

.cart-summary {
  margin-top: 30px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.label {
  font-size: 16px;
  color: #666;
}

.total-price {
  font-size: 28px;
  color: #f56c6c;
  font-weight: bold;
}

.balance {
  font-size: 18px;
  color: #67c23a;
  font-weight: 500;
}
</style>
