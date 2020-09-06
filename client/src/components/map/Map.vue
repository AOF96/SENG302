<template>
  <div id="map">
  </div>
</template>

<script>
  export default {
    name: "Map",
    data: function() {
      return {
        map: null,
      }
    },
    mounted() {
      this.map = new window.google.maps.Map(document.getElementById("map"), {
        center: {
          lat: -34.397,
          lng: 150.644
        },
        zoom: 8
      });
    }
  }
</script>

<style scoped>
  @import "../../../public/styles/pages/mapStyle.css";
</style>