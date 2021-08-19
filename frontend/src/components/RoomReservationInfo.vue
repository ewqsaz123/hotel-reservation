<template>

<v-data-table
    :headers="headers"
    :items="roomReservationInfo"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'RoomReservationInfo',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "id", value: "id" },
            { text: "roomId", value: "roomId" },
            { text: "reservationId", value: "reservationId" },
            { text: "roomStatus", value: "roomStatus" },
            { text: "customerName", value: "customerName" },
            { text: "customerId", value: "customerId" },
            { text: "roomName", value: "roomName" },
        ],
        roomReservationInfo : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/roomreservationinfos')

      this.roomReservationInfo = temp.data._embedded.roomreservationinfos;

    },
    methods: {
    }
  }
</script>

