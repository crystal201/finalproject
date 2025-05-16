<template>
    <div v-if="isOpen" class="modal-overlay">
      <div class="modal-content">
        <h2 class="modal-title">Xác nhận đặt vé</h2>
        <div class="modal-body">
          <div class="summary-item">
            <span class="label">Phim:</span>
            <span class="value">{{ bookingSummary.movieTitle || '--' }}</span>
          </div>
          <div class="summary-item">
            <span class="label">Suất chiếu:</span>
            <span class="value">
              {{ bookingSummary.showtime || '--' }} 
              <span v-if="bookingSummary.date">({{ bookingSummary.date }})</span>
            </span>
          </div>
          <div class="summary-item">
            <span class="label">Ghế:</span>
            <span class="value">{{ bookingSummary.seats.join(", ") || "Chưa chọn" }}</span>
          </div>
          <div class="summary-item total">
            <span class="label">Tổng cộng:</span>
            <span class="value">{{ bookingSummary.total }}.000đ</span>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="confirm" class="modal-button primary">Xác nhận</button>
          <button @click="cancel" class="modal-button secondary">Hủy</button>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      isOpen: {
        type: Boolean,
        default: false,
      },
      bookingSummary: {
        type: Object,
        default: () => ({}),
      },
    },
    methods: {
      confirm() {
        this.$emit('confirm');
      },
      cancel() {
        this.$emit('cancel');
      },
    },
  };
  </script>
  
  <style scoped>
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }
  
  .modal-content {
    background-color: white;
    padding: 20px;
    border-radius: 8px;
    width: 90%;
    max-width: 400px;
    text-align: center;
  }
  
  .modal-title {
    font-size: 1.5rem;
    font-weight: bold;
    margin-bottom: 15px;
    color: #333;
  }
  
  .modal-body {
    margin-bottom: 20px;
  }
  
  .summary-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    font-size: 1rem;
    color: #666;
  }
  
  .summary-item.total {
    font-weight: bold;
    color: #000;
  }
  
  .label {
    font-weight: 500;
  }
  
  .value {
    text-align: right;
  }
  
  .modal-actions {
    display: flex;
    justify-content: center;
    gap: 10px;
  }
  
  .modal-button {
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 1rem;
  }
  
  .modal-button.primary {
    background-color: #3B82F6;
    color: white;
  }
  
  .modal-button.secondary {
    background-color: #E5E7EB;
    color: #333;
  }
  
  .modal-button:hover {
    opacity: 0.9;
  }
  </style>