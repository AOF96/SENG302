<template>
    <div id="settingsNavWrap">
      <NavBar/>
      <h1>Settings</h1>
      <div id="settingsNav">
        <router-link to="/settings/profile"><div class="settingsNavItem">Profile</div></router-link>
        <hr>
        <router-link to="/settings/email"><div class="settingsNavItem">Emails</div></router-link>
        <hr>
        <router-link to="/settings/password"><div class="settingsNavItem">Password</div></router-link>
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