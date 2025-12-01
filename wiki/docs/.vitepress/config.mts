import { defineConfig } from 'vitepress'

const isGitHubActions = process.env.GITHUB_ACTIONS === 'true'
const base = isGitHubActions ? '/wiki/' : '/'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  base,
  title: "Sumer Wars Wiki",
  description: "Official Wiki of the MMORTS Sumer Wars - Master the art of war in ancient Mesopotamia",
  appearance: 'force-dark',
  themeConfig: {
    search: {
      provider: 'local'
    },
    
    nav: [
      { text: '🏠 Home', link: '/' },
      { text: '📖 Guide', link: '/guide' },
    ],

    sidebar: {
      // '/guide/': [
      //   {
      //     text: '📖 Player Guide',
      //     items: [
      //       { text: 'Introduction', link: '/guide/' },
      //       { text: 'Quick Start', link: '/guide/getting-started' },
      //       { text: 'River System', link: '/guide/rivers' },
      //       { text: 'Trade & Economy', link: '/guide/economy' },
      //       { text: 'Warfare & Strategy', link: '/guide/combat' },
      //       { text: 'Alliances', link: '/guide/alliances' },
      //       { text: 'PvE & Exploration', link: '/guide/pve' },
      //       { text: 'Victory Conditions', link: '/guide/victory' }
      //     ]
      //   }
      // ],
      '/': [
        {
          text: '🏗️ Construction',
          items: [
            { text: '🏛️ Buildings', link: '/buildings' },
            { text: '🌾 Resources', link: '/resources' }
          ]
        },
        {
          text: '⚔️ Military',
          items: [
            { text: 'Land Units', link: '/units' },
            { text: 'River Units', link: '/units-naval' },
            { text: 'Warfare & Strategy', link: '/guide/combat' }
          ]
        },
        // {
        //   text: '🎮 Gameplay',
        //   items: [
        //     { text: 'Beginner Guide', link: '/guide/' },
        //     { text: 'River System', link: '/guide/rivers' },
        //     { text: 'PvE & Exploration', link: '/guide/pve' },
        //     { text: 'Alliances', link: '/guide/alliances' }
        //   ]
        // }
      ]
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/Sumer-Wars/wiki' }
    ],
    
    footer: {
      message: 'Wiki of Sumer Wars',
      copyright: 'Copyright © 2025 Sumer Wars'
    }
  }
})
