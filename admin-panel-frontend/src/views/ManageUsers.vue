<template>
  <div class="manage-users">
    <div class="page-header">
      <h2><i class="fas fa-users"></i> User Management</h2>
      <div class="header-actions">
        <button @click="fetchUsers" class="refresh-btn">
          <i class="fas fa-sync-alt"></i> Refresh
        </button>
      </div>
    </div>
    
    <div class="card">
      <div class="table-responsive">
        <table class="users-table">
          <thead>
            <tr>
              <th>User</th>
              <th>Email</th>
              <th>Role</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>
                <div class="user-info">
                  <div class="avatar">
                    <i class="fas fa-user"></i>
                  </div>
                  <div class="user-details">
                    <strong>{{ user.username }}</strong>
                    <span>ID: {{ user.id }}</span>
                  </div>
                </div>
              </td>
              <td>{{ user.email }}</td>
              <td>
                <span class="role-badge" :class="user.role.toLowerCase()">{{ user.role }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div class="table-footer">
        <div class="table-info">
          Showing {{ users.length }} of {{ users.length }} users
        </div>
        <div class="pagination">
          <button class="pagination-btn" disabled>
            <i class="fas fa-chevron-left"></i>
          </button>
          <span class="current-page">1</span>
          <button class="pagination-btn" disabled>
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>
    </div>
    
  </div>
</template>

<script>
export default {
  data() {
    return {
      users: [],
      loading: false
    }
  },
  methods: {
    async fetchUsers() {
      this.loading = true;
      try {
        const response = await this.axios.get('/api/users');
        this.users = response.data;
      } catch (error) {
        console.error("API Error:", error.response ? error.response.data : error.message);
        this.users = [];
      } finally {
        this.loading = false;
      }
    },

  },
  mounted() {
    this.fetchUsers();
  }
}
</script>

<style scoped>
.manage-users {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 1.5rem;
  color: var(--light);
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-header h2 i {
  color: var(--primary);
}

.header-actions {
  display: flex;
  gap: 10px;
}

.refresh-btn {
  background: var(--card-bg);
  color: var(--light);
  border: none;
  padding: 8px 15px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.refresh-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.refresh-btn i {
  font-size: 0.9rem;
}

.card {
  background: var(--card-bg);
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.table-responsive {
  overflow-x: auto;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
}

.users-table th,
.users-table td {
  padding: 15px;
  text-align: left;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.users-table th {
  background: rgba(255, 255, 255, 0.03);
  color: var(--gray);
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.8rem;
  letter-spacing: 0.5px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-details span {
  font-size: 0.8rem;
  color: var(--gray);
}

.role-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.role-badge.admin {
  background: rgba(99, 102, 241, 0.1);
  color: var(--primary);
}

.role-badge.staff {
  background: rgba(244, 63, 94, 0.1);
  color: var(--secondary);
}

.role-badge.user {
  background: rgba(16, 185, 129, 0.1);
  color: var(--success);
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: rgba(255, 255, 255, 0.03);
}

.table-info {
  color: var(--gray);
  font-size: 0.85rem;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 5px;
}

.pagination-btn {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.05);
  border: none;
  color: var(--gray);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pagination-btn:not(:disabled):hover {
  background: rgba(255, 255, 255, 0.1);
  color: var(--light);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.current-page {
  min-width: 32px;
  text-align: center;
  font-weight: 600;
}

/* Modal Styles */
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

.modal {
  background: var(--card-bg);
  border-radius: 10px;
  width: 100%;
  max-width: 450px;
  overflow: hidden;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.modal-header {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-header h3 {
  font-size: 1.2rem;
  color: var(--light);
}

.close-modal {
  background: none;
  border: none;
  color: var(--gray);
  font-size: 1.2rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.close-modal:hover {
  color: var(--light);
}

.modal-body {
  padding: 20px;
  color: var(--gray);
  line-height: 1.6;
}

.modal-body p {
  margin-bottom: 10px;
}

.modal-body strong {
  color: var(--light);
}

.modal-footer {
  padding: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.cancel-btn {
  background: rgba(255, 255, 255, 0.05);
  color: var(--light);
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.confirm-btn {
  background: var(--danger);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.confirm-btn:hover {
  background: #dc2626;
}

/* Responsive Design */
@media (max-width: 768px) {
  .users-table th,
  .users-table td {
    padding: 12px 10px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>