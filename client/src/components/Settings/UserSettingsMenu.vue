<template>
    <div id="settingsNavWrap">
      <NavBar/>
      <h1>Settings</h1>
      <div id="settingsNav">
        <router-link  v-bind:to="'/settings/profile?u=' + this.$route.query.u"><div class="settingsNavItem">Profile</div></router-link>
        <hr>
        <router-link v-bind:to="'/settings/email?u=' + this.$route.query.u"><div class="settingsNavItem">Emails</div></router-link>
        <hr>
        <router-link v-bind:to="'/settings/password?u=' + this.$route.query.u"><div class="settingsNavItem">Password</div></router-link>
        <hr>
        <router-link v-bind:to="'/settings/passport_countries?u=' + this.$route.query.u"><div class="settingsNavItem">Passport Countries</div></router-link>
      </div>
    </div>
</template>

<script>
  import NavBar from '@/components/NavBar'
  export default {
    components: {
        NavBar
    },
  }
</script>