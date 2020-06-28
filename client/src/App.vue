<template>
  <v-app>
    <v-content>
<!--      <router-view/>-->
      <div id="appContainer">
        <div id="pageContent">
          <router-view/>
        </div>
      </div>
    </v-content>
  </v-app>
</template>

<script>

export default {
  name: 'App',

  data: () => ({
    //
  }),
};
</script>
