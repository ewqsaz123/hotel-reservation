<template>

<v-data-table
    :headers="headers"
    :items="resevationStatusView"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'ResevationStatusView',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "id", value: "id" },
            { text: "customerId", value: "customerId" },
            { text: "customerName", value: "customerName" },
            { text: "reservationId", value: "reservationId" },
            { text: "hotelId", value: "hotelId" },
            { text: "hotelName", value: "hotelName" },
            { text: "roomId", value: "roomId" },
            { text: "roomName", value: "roomName" },
            { text: "roomPrice", value: "roomPrice" },
            { text: "checkInDate", value: "checkInDate" },
            { text: "checkOutDate", value: "checkOutDate" },
            { text: "reservationStatus", value: "reservationStatus" },
            { text: "paymentStatus", value: "paymentStatus" },
        ],
        resevationStatusView : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/resevationstatusviews')

      this.resevationStatusView = temp.data._embedded.resevationstatusviews;

    },
    methods: {
    }
  }
</script>

