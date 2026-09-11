<template>
  <div class="user-center-container">
    <el-header class="header">
      <div class="header-content">
        <h1>个人中心</h1>
        <el-button @click="$router.push('/')">返回首页</el-button>
      </div>
    </el-header>

    <el-main class="main-content">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="info-card">
            <template #header>
              <div class="card-header">
                <span>账户信息</span>
              </div>
            </template>
            <div class="info-item">
              <span class="label">用户名：</span>
              <span class="value">{{ userStore.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">余额：</span>
              <span class="value balance">¥{{ balance }}</span>
            </div>
            <el-button type="primary" @click="showRechargeDialog" style="width: 100%; margin-top: 20px">
              充值
            </el-button>
          </el-card>
        </el-col>

        <el-col :span="16">
          <el-card class="order-card">
            <template #header>
              <div class="card-header">
                <span>我的订单</span>
              </div>
            </template>

            <el-table :data="orders" stripe style="width: 100%">
              <el-table-column prop="orderNo" label="订单号" width="180" />
              <el-table-column prop="totalPrice" label="总价" width="120">
                <template #default="{ row }">¥{{ row.totalPrice }}</template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'PENDING' ? 'warning' : 'success'">
                    {{ row.status === 'PENDING' ? '未完成' : '已完成' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="下单时间" width="180">
                <template #default="{ row }">
                  {{ formatDateTime(row.createdAt) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button size="small" @click="showOrderDetail(row.id)">查看详情</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>

          <el-card class="favorite-card">
            <template #header>
              <div class="card-header">
                <span>我的收藏</span>
              </div>
            </template>

            <el-table :data="favorites" stripe style="width: 100%" v-loading="favoritesLoading">
              <el-table-column prop="dishName" label="菜名" width="140" />
              <el-table-column prop="price" label="价格" width="100">
                <template #default="{ row }">¥{{ row.price }}</template>
              </el-table-column>
              <el-table-column prop="description" label="简介" show-overflow-tooltip />
              <el-table-column label="状态" width="90">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'ON_SHELF' ? 'success' : 'info'">
                    {{ row.status === 'ON_SHELF' ? '在售' : '已下架' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="收藏时间" width="170">
                <template #default="{ row }">
                  {{ formatDateTime(row.createdAt) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180">
                <template #default="{ row }">
                  <el-button
                    size="small"
                    type="primary"
                    :disabled="row.status !== 'ON_SHELF'"
                    @click="addToCart(row)"
                  >加入购物车</el-button>
                  <el-button size="small" type="danger" @click="removeFavorite(row.dishId)">取消收藏</el-button>
                </template>
              </el-table-column>
              <template #empty>
                <el-empty description="暂无收藏" />
              </template>
            </el-table>

            <div class="pagination-wrapper" v-if="favoriteTotal > 0">
              <el-pagination
                v-model:current-page="favoritePage"
                v-model:page-size="favoriteSize"
                :total="favoriteTotal"
                :page-sizes="[5, 10, 20]"
                layout="total, sizes, prev, pager, next"
                @current-change="loadFavorites"
                @size-change="handleFavoriteSizeChange"
              />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-main>

    <el-dialog v-model="rechargeVisible" title="充值" width="400px">
      <el-form :model="rechargeForm" :rules="rechargeRules" ref="rechargeFormRef" label-width="100px">
        <el-form-item label="充值金额" prop="amount">
          <el-input-number v-model="rechargeForm.amount" :min="1" :max="10000" :precision="2" :step="10" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rechargeVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRecharge" :loading="rechargeLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="总价">¥{{ currentOrder.totalPrice }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          {{ currentOrder.status === 'PENDING' ? '未完成' : '已完成' }}
        </el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ formatDateTime(currentOrder.createdAt) }}</el-descriptions-item>
      </el-descriptions>

      <h3 style="margin-top: 20px">菜品明细</h3>
      <el-table :data="orderItems" stripe style="width: 100%">
        <el-table-column prop="dishName" label="菜名" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="小计" width="100">
          <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '../store/user'
import { userApi, orderApi, favoriteApi } from '../api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const balance = ref(userStore.balance)
const orders = ref([])
const rechargeVisible = ref(false)
const rechargeLoading = ref(false)
const rechargeFormRef = ref(null)
const detailVisible = ref(false)
const orderItems = ref([])
const favorites = ref([])
const favoritesLoading = ref(false)
const favoritePage = ref(1)
const favoriteSize = ref(5)
const favoriteTotal = ref(0)

const rechargeForm = reactive({
  amount: 100
})

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace('T', ' ').substring(0, 19)
}

const rechargeRules = {
  amount: [
    { required: true, message: '请输入充值金额', trigger: 'blur' },
    { type: 'number', min: 1, max: 10000, message: '充值金额在 1 ~ 10000 之间', trigger: 'blur' }
  ]
}

const currentOrder = reactive({
  orderNo: '',
  totalPrice: 0,
  status: '',
  createdAt: ''
})

const loadUserInfo = async () => {
  try {
    const res = await userApi.getUserInfo()
    balance.value = res.data.balance
    userStore.updateBalance(res.data.balance)
  } catch (error) {
    ElMessage.error('加载用户信息失败')
  }
}

const loadOrders = async () => {
  try {
    const res = await orderApi.getMyOrders()
    orders.value = res.data
  } catch (error) {
    ElMessage.error('加载订单失败')
  }
}

const showRechargeDialog = () => {
  rechargeForm.amount = 100
  rechargeVisible.value = true
}

const handleRecharge = async () => {
  await rechargeFormRef.value.validate(async (valid) => {
    if (!valid) return

    rechargeLoading.value = true
    try {
      const res = await userApi.recharge(rechargeForm.amount)
      balance.value = res.data
      userStore.updateBalance(res.data)
      ElMessage.success('充值成功')
      rechargeVisible.value = false
    } catch (error) {
      ElMessage.error(error.message || '充值失败')
    } finally {
      rechargeLoading.value = false
    }
  })
}

const showOrderDetail = async (orderId) => {
  try {
    const order = orders.value.find(o => o.id === orderId)
    Object.assign(currentOrder, order)

    const res = await orderApi.getOrderItems(orderId)
    orderItems.value = res.data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('加载订单详情失败')
  }
}

const loadFavorites = async () => {
  favoritesLoading.value = true
  try {
    const res = await favoriteApi.getMyFavorites(favoritePage.value, favoriteSize.value)
    favorites.value = res.data.list
    favoriteTotal.value = res.data.total

    // 当前页被删空且不是第一页时，回退一页
    if (favorites.value.length === 0 && favoritePage.value > 1) {
      favoritePage.value -= 1
      await loadFavorites()
    }
  } catch (error) {
    ElMessage.error('加载收藏失败')
  } finally {
    favoritesLoading.value = false
  }
}

const handleFavoriteSizeChange = () => {
  favoritePage.value = 1
  loadFavorites()
}

const removeFavorite = async (dishId) => {
  try {
    await favoriteApi.remove(dishId)
    ElMessage.success('已取消收藏')
    loadFavorites()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const addToCart = (dish) => {
  if (dish.status !== 'ON_SHELF') {
    ElMessage.warning('该菜品已下架，无法加入购物车')
    return
  }

  const cart = JSON.parse(localStorage.getItem('cart') || '[]')
  const existingItem = cart.find(item => item.dishId === dish.dishId)

  if (existingItem) {
    existingItem.quantity += 1
  } else {
    cart.push({
      dishId: dish.dishId,
      dishName: dish.dishName,
      price: dish.price,
      quantity: 1
    })
  }

  localStorage.setItem('cart', JSON.stringify(cart))
  ElMessage.success('已加入购物车')
}

onMounted(() => {
  loadUserInfo()
  loadOrders()
  loadFavorites()
})
</script>

<style scoped>
.user-center-container {
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

.info-card, .order-card {
  border-radius: 12px;
}

.favorite-card {
  border-radius: 12px;
  margin-top: 20px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  color: #666;
}

.value {
  color: #333;
  font-weight: 500;
}

.balance {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}
</style>
