import { defineConfig, presetUno, presetIcons } from 'unocss'

export default defineConfig({
  presets: [
    presetUno({ preflight: false }),
    presetIcons(),
  ],
})
