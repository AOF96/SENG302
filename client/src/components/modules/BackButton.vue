<template>
  <v-btn v-on:click="goToPreviousPage"
         depressed
         v-if="getPreviousPage != null"
  >
    <v-icon medium color="primary">mdi-arrow-left</v-icon>
    Back
  </v-btn>
</template>

<script>
  import {mapGetters} from 'vuex';
  import router from "../../router";

  export default {
    name: "BackButton",
    computed: {
      ...mapGetters(["getPreviousPage"])
    },
    methods: {
      /**
       * Routes to the previous page from history
       */
      goToPreviousPage() {
        router.push(this.getPreviousPage);
      }
    }
  }
</script>

<style scoped>

</style>