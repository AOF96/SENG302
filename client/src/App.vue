<template>
  <v-app>
    <v-content>
      <div id="appContainer">
        <div id="pageContent">
          <NavBar/>
          <router-view/>
        </div>
      </div>
    </v-content>
  </v-app>
</template>

<script>
import NavBar from "./components/modules/NavBar";
export default {
  name: 'App',
  components: {
    NavBar
  },
  data: () => ({
    //
  }),
};
</script>
