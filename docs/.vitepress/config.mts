import {DefaultTheme, defineConfig} from 'vitepress'

const pkg = require('../package.json')

// https://vitepress.dev/reference/site-config
export default defineConfig({
    head: [
        ['meta', {name: 'theme-color', content: '#389BFF'}],
        ['link', {rel: 'icon', type: 'image/svg+xml', href: '/pluginIcon.svg'}],
        [
            'script',
            {},
            `var _hmt = _hmt || [];
            (function() {
              var hm = document.createElement("script");
              hm.src = "https://hm.baidu.com/hm.js?40a7c38d178d1f141a76eaae0c813277";
              var s = document.getElementsByTagName("script")[0]; 
              s.parentNode.insertBefore(hm, s);
            })();`
        ]
    ],
    title: 'DebugTools | Java调试工具',
    titleTemplate: 'DebugTools',
    description: "快速调用任意Java方法(本地/远程)、打印SQL语句与耗时、执行Groovy脚本",
    themeConfig: {
        siteTitle: 'DebugTools',
        logo: '/pluginIcon.svg',
        // https://vitepress.dev/reference/default-theme-config
        nav: nav(),
        outline: {
            level: 'deep',
            label: '页面导航'
        },
        editLink: {
            pattern: 'https://github.com/future0923/debug-tools/edit/main/docs/:path',
            text: '在 GitHub 上编辑此页面'
        },
        docFooter: {
            prev: '上一页',
            next: '下一页'
        },
        lastUpdated: {
            text: '最后更新于',
        },
        sidebar: {
            'guide': {
                base: '/guide/',
                items: sidebarGuide()
            },
        },
        socialLinks: [
            {icon: 'github', link: 'https://github.com/future0923/debug-tools'}
        ],
        footer: {
            message: '基于 Apache 许可发布',
            copyright: `版权所有 © 2024-${new Date().getFullYear()} Future0923`
        },
        search: {
            provider: 'algolia',
            options: {
                placeholder: '搜索文档',
                translations: {
                    button: {
                        buttonText: '搜索文档',
                        buttonAriaLabel: '搜索文档'
                    },
                    modal: {
                        searchBox: {
                            resetButtonTitle: '清除查询条件',
                            resetButtonAriaLabel: '清除查询条件',
                            cancelButtonText: '取消',
                            cancelButtonAriaLabel: '取消'
                        },
                        startScreen: {
                            recentSearchesTitle: '搜索历史',
                            noRecentSearchesText: '没有搜索历史',
                            saveRecentSearchButtonTitle: '保存至搜索历史',
                            removeRecentSearchButtonTitle: '从搜索历史中移除',
                            favoriteSearchesTitle: '收藏',
                            removeFavoriteSearchButtonTitle: '从收藏中移除'
                        },
                        errorScreen: {
                            titleText: '无法获取结果',
                            helpText: '你可能需要检查你的网络连接'
                        },
                        footer: {
                            selectText: '选择',
                            navigateText: '切换',
                            closeText: '关闭',
                            searchByText: '搜索提供者'
                        },
                        noResultsScreen: {
                            noResultsText: '无法找到相关结果',
                            suggestedQueryText: '你可以尝试查询',
                            reportMissingResultsText: '你认为该查询应该有结果？',
                            reportMissingResultsLinkText: '点击反馈'
                        }
                    }
                }
            }
        }
    }
})

function nav(): DefaultTheme.NavItem[] {
    return [
        {
            text: '文档',
            link: '/guide/introduction',
            activeMatch: '/guide/'
        },
        {
            text: pkg.version,
            items: [
                {
                    text: '更新日志',
                    link: 'https://github.com/future0923/debug-tools/releases'
                },
                {
                    text: 'GitHub',
                    link: 'https://github.com/future0923/debug-tools'
                }
            ]
        }
    ]
}

function sidebarGuide(): DefaultTheme.SidebarItem[] {
    return [
        {
            text: '简介',
            collapsed: false,
            items: [
                {text: '什么是 DebugTools？', link: 'introduction'},
                {text: '快速开始', link: 'quick-start'}
            ]
        },
        {
            text: '快速调用Java方法',
            collapsed: false,
            items: [
                {text: '本地方法', link: 'local-method'},
                {text: '远程方法', link: 'remote-method'},
                {text: '类加载器', link: 'classloader'},
                {text: '参数数据格式', link: 'debug-tools-json'},
                {text: '参数导入导出', link: 'param-operation'},
                {text: 'header参数', link: 'header'},
                {text: 'xxl-job参数', link: 'xxl-job'},
                {text: '快捷调用上一次', link: 'execute-last'},
            ]
        },
        {
            text: '打印SQL语句与执行耗时',
            collapsed: false,
            items: [
                {text: 'SQL', link: 'sql'},
            ]
        },
        {
            text: '执行Groovy脚本',
            collapsed: false,
            items: [
                {text: '执行Groovy脚本', link: 'groovy-execute'},
                {text: '内置函数', link: 'groovy-function'},
            ]
        },
        {
            text: 'Idea插件说明',
            collapsed: false,
            items: [
                {text: 'ToolsWindow', link: 'idea-tools-window'},
                {text: '右键菜单', link: 'idea-menu'},
                {text: '配置项', link: 'idea-config'},
                {text: '调试面板', link: 'idea-panel'},
                {text: '展示结果', link: 'idea-result'},
            ]
        },
    ]
}