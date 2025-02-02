<script setup lang="ts"></script>

<template>
    <svg viewBox="0 0 512 512">
        <polygon class="arrow-left" points="51.2,166.4 243.2,166.4 243.2,64 51.2,64 0,115.2 " />
        <polygon
            class="arrow-right"
            points="460.8,268.8 268.8,268.8 268.8,166.4 460.8,166.4 512,217.6 "
        />
        <rect class="post" x="230.4" y="25.6" width="51.2" height="460.8" />
        <rect class="post-shade" x="256" y="25.6" width="25.6" height="460.8" />
    </svg>
</template>

<style scoped>
.arrow-left {
    fill: hsl(var(--destructive));
}

.arrow-right {
    fill: hsl(var(--primary));
}

.post {
    fill: hsl(var(--muted));
}

.post-shade {
    fill: hsla(var(--secondary-foreground));
    opacity: 0.1;
}
</style>
