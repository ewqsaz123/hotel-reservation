<template>

<v-data-table
    :headers="headers"
    :items="rentalStatusView"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'RentalStatusView',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "id", value: "id" },
            { text: "rentalId", value: "rentalId" },
            { text: "bookId", value: "bookId" },
            { text: "rentalStatus", value: "rentalStatus" },
        ],
        rentalStatusView : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/rentalstatusviews')

      this.rentalStatusView = temp.data._embedded.rentalstatusviews;

    },
    methods: {
    }
  }
</script>

