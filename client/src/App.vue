<template>
  <v-app>
    <v-content>
      <div id="appContainer">
        <div id="pageContent">
          <NavBar v-on:set-theme="updateTheme"/>
          <router-view v-bind:dark-mode-global="darkModeGlobal"/>
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
    darkModeGlobal: false
  }),
  methods: {
    /**
     * Updates global theme variable that is used as a prop on other pages
     */
    updateTheme() {
      this.darkModeGlobal = (localStorage.getItem("darkMode") === 'true')
    }
  }
};
</script>
