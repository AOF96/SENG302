<template>
  <div id="appContainer">
    <div id="pageContent">
        <router-view/>
    </div>
  </div>
</template>


<script>
  const app = {
    name: 'app',
    data: () => {
      return {
      }
    }
  };

  export default app
</script>

<style>
  [v-cloak] { display: none; }
</style>
