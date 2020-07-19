<template>
  <v-btn v-on:click="goToNextPage"
         depressed
         v-if="getNextPage != null"
  >
    Forward
    <v-icon medium color="primary">mdi-arrow-right</v-icon>
  </v-btn>
</template>

<script>
  import {mapGetters} from 'vuex';
  import router from "../../router";

  export default {
    name: "ForwardButton",
    computed: {
      ...mapGetters(["getNextPage"])
    },
    methods: {
      /**
       * Routes to the next page from history
       */
      goToNextPage() {
        router.push(this.getNextPage);
      }
    }
  }
</script>

<style scoped>

</style>