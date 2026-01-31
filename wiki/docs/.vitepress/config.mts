import { defineConfig } from 'vitepress'

const isGitHubActions = process.env.GITHUB_ACTIONS === 'true'
const base = isGitHubActions ? '/rise-of-babylon/' : '/'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  base,
  title: "Rise Of Babylon Wiki",
  description: "Official Wiki of the MMORTS Rise Of Babylon - Master the art of war in ancient Mesopotamia",
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
      { icon: 'github', link: 'https://github.com/Xamez/rise-of-babylon' }
    ],
    
    footer: {
      message: 'Wiki of Rise Of Babylon',
      copyright: `Copyright © ${new Date().getFullYear()} Rise Of Babylon`
    }
  }
})
