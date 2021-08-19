<template>

<v-data-table
    :headers="headers"
    :items="reservationInfo"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'ReservationInfo',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "id", value: "id" },
            { text: "reservationId", value: "reservationId" },
            { text: "hotelName", value: "hotelName" },
            { text: "roomName", value: "roomName" },
            { text: "reservationStatus", value: "reservationStatus" },
            { text: "roomPrice", value: "roomPrice" },
        ],
        reservationInfo : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/reservationinfos')

      this.reservationInfo = temp.data._embedded.reservationinfos;

    },
    methods: {
    }
  }
</script>

