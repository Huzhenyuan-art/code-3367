<template>
  <div class="admin-container">
    <el-header class="header">
      <div class="header-content">
        <h1>菜品管理</h1>
        <div>
        <el-button style="margin-right: 6px;" @click="$router.push('/admin/orders')">订单管理</el-button>
        <el-button @click="$router.push('/')">返回首页</el-button>
        </div>
      </div>
    </el-header>

    <el-main class="main-content">

      <el-table :data="dishes" stripe style="width: 100%">
        <el-table-column prop="dishName" label="菜名" width="160" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="description" label="简介" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ON_SHELF' ? 'success' : 'info'">
              {{ row.status === 'ON_SHELF' ? '在售' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260">
          <template #default="{ row }">
            <el-button size="small" @click="showEditDialog(row)">修改</el-button>
            <el-button
              size="small"
              :type="row.status === 'ON_SHELF' ? 'warning' : 'success'"
              @click="handleToggleStatus(row)"
            >{{ row.status === 'ON_SHELF' ? '下架' : '上架' }}</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
            <el-button type="primary" @click="showAddDialog" style="margin-top: 20px">添加菜品</el-button>

    </el-main>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="菜名" prop="dishName">
          <el-input v-model="form.dishName" placeholder="请输入菜名" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0.01" :max="99999" :precision="2" :step="1" />
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入简介" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { dishApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const dishes = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const loading = ref(false)
const editingId = ref(null)

const form = reactive({
  dishName: '',
  price: 0,
  description: ''
})

const rules = {
  dishName: [
    { required: true, message: '请输入菜名', trigger: 'blur' },
    { max: 100, message: '菜名不能超过100个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '价格必须大于0', trigger: 'blur' }
  ]
}

const loadDishes = async () => {
  try {
    const res = await dishApi.getAll()
    dishes.value = res.data
  } catch (error) {
    ElMessage.error('加载菜品失败')
  }
}

const handleToggleStatus = async (dish) => {
  const newStatus = dish.status === 'ON_SHELF' ? 'OFF_SHELF' : 'ON_SHELF'
  try {
    await dishApi.updateStatus(dish.id, newStatus)
    ElMessage.success(newStatus === 'ON_SHELF' ? '已上架' : '已下架')
    loadDishes()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const showAddDialog = () => {
  dialogTitle.value = '添加菜品'
  editingId.value = null
  form.dishName = ''
  form.price = 0
  form.description = ''
  dialogVisible.value = true
}

const showEditDialog = (dish) => {
  dialogTitle.value = '修改菜品'
  editingId.value = dish.id
  form.dishName = dish.dishName
  form.price = dish.price
  form.description = dish.description
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      if (editingId.value) {
        await dishApi.update(editingId.value, form)
        ElMessage.success('修改成功')
      } else {
        await dishApi.add(form)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadDishes()
    } catch (error) {
      ElMessage.error(error.message || '操作失败')
    } finally {
      loading.value = false
    }
  })
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该菜品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await dishApi.delete(id)
    ElMessage.success('删除成功')
    loadDishes()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadDishes()
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
