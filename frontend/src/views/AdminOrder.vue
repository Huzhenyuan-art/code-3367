<template>
  <div class="admin-container">
    <el-header class="header">
      <div class="header-content">
        <h1>订单管理</h1>
        <el-button @click="$router.push('/')">返回首页</el-button>
      </div>
    </el-header>

    <el-main class="main-content">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="未完成订单" name="PENDING">
          <el-table :data="pendingOrders" stripe style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column prop="totalPrice" label="总价" width="120">
              <template #default="{ row }">¥{{ row.totalPrice }}</template>
            </el-table-column>
            <el-table-column prop="createdAt" label="下单时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="订单详情" width="200">
              <template #default="{ row }">
                <el-button size="small" @click="showOrderDetail(row.id)">查看详情</el-button>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button size="small" type="success" @click="completeOrder(row.id)">完成</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="已完成订单" name="COMPLETED">
          <el-table :data="completedOrders" stripe style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column prop="totalPrice" label="总价" width="120">
              <template #default="{ row }">¥{{ row.totalPrice }}</template>
            </el-table-column>
            <el-table-column prop="createdAt" label="下单时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="订单详情" width="200">
              <template #default="{ row }">
                <el-button size="small" @click="showOrderDetail(row.id)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-main>

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
import { ref, reactive, onMounted, computed } from 'vue'
import { orderApi } from '../api'
import { ElMessage } from 'element-plus'

const activeTab = ref('PENDING')
const orders = ref([])
const orderItems = ref([])

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace('T', ' ').substring(0, 19)
}
const detailVisible = ref(false)
const currentOrder = reactive({
  orderNo: '',
  totalPrice: 0,
  status: '',
  createdAt: ''
})

const pendingOrders = computed(() => orders.value.filter(o => o.status === 'PENDING'))
const completedOrders = computed(() => orders.value.filter(o => o.status === 'COMPLETED'))

const loadOrders = async () => {
  try {
    const res = await orderApi.getAllOrders()
    orders.value = res.data
  } catch (error) {
    ElMessage.error('加载订单失败')
  }
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

const completeOrder = async (orderId) => {
  try {
    await orderApi.updateStatus(orderId, 'COMPLETED')
    ElMessage.success('订单已完成')
    loadOrders()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.admin-container {
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
</style>
