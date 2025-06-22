<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-gray-800">Occupied Seats</h2>
      <button 
        @click="fetchOccupiedSeats" 
        class="button button-secondary flex items-center gap-2"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M4 2a1 1 0 011 1v2.101a7.002 7.002 0 0111.601 2.566 1 1 0 11-1.885.666A5.002 5.002 0 005.999 7H9a1 1 0 010 2H4a1 1 0 01-1-1V3a1 1 0 011-1zm.008 9.057a1 1 0 011.276.61A5.002 5.002 0 0014.001 13H11a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0v-2.101a7.002 7.002 0 01-11.601-2.566 1 1 0 01.61-1.276z" clip-rule="evenodd" />
        </svg>
        Refresh
      </button>
    </div>

    <div class="bg-white shadow overflow-hidden sm:rounded-lg">
      <ul class="divide-y divide-gray-200">
        <li v-for="seat in occupiedSeats" :key="seat.seat + seat.date + seat.showtime" class="px-4 py-4 sm:px-6">
          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <div class="min-w-0 flex-1 flex items-center">
                <div class="flex-shrink-0 h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-600" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M6 6V5a3 3 0 013-3h2a3 3 0 013 3v1h2a2 2 0 012 2v3.57A22.952 22.952 0 0110 13a22.95 22.95 0 01-8-1.43V8a2 2 0 012-2h2zm2-1a1 1 0 011-1h2a1 1 0 011 1v1H8V5zm1 5a1 1 0 011-1h.01a1 1 0 110 2H10a1 1 0 01-1-1z" clip-rule="evenodd" />
                  </svg>
                </div>
                <div class="min-w-0 flex-1 px-4">
                  <div>
                    <p class="text-sm font-medium text-indigo-600 truncate">Seat {{ seat.seat }}</p>
                    <p class="mt-1 text-sm text-gray-500">
                      Room {{ seat.roomId }} • {{ seat.date }} at {{ seat.showtime }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </li>
      </ul>
    </div>

    <div v-if="occupiedSeats.length === 0" class="text-center py-12">
      <p class="text-gray-500">No occupied seats found.</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      occupiedSeats: []
    }
  },
  methods: {
    async fetchOccupiedSeats() {
      try {
        const response = await axios.get('/api/bookings/occupied-seats');
        this.occupiedSeats = response.data;
      } catch (error) {
        console.error("API Error:", error);
        this.occupiedSeats = [];
      }
    }
  },
  mounted() {
    this.fetchOccupiedSeats();
  }
}
</script>