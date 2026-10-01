package dev.remodded.rewhitelist.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor

/**
 * Minimal chat pagination, modeled after the (now unmaintained) adventure-text-feature-pagination.
 */
class Pagination<T>(
    private val title: Component,
    private val width: Int = 50,
    private val resultsPerPage: Int = 6,
    private val pageCommand: (Int) -> String,
    private val rowRenderer: (T, Int) -> List<Component>,
) {

    fun render(content: List<T>, page: Int): List<Component> {
        if (content.isEmpty())
            return listOf(renderEmpty())

        val pages = (content.size + resultsPerPage - 1) / resultsPerPage
        if (page !in 1..pages)
            return listOf(Component.text("Unknown page selected. $pages total pages.", NamedTextColor.GRAY))

        val start = resultsPerPage * (page - 1)
        val end = minOf(start + resultsPerPage, content.size)

        return buildList {
            add(centered(renderHeader(page, pages)))
            for (i in start..<end)
                addAll(rowRenderer(content[i], i))
            add(renderFooter(page, pages))
        }
    }

    private fun renderEmpty(): Component {
        return Component.text()
            .append(centered(renderHeader(0, 0)))
            .append(Component.newline())
            .append(Component.text("NO ENTRIES", NamedTextColor.GRAY))
            .append(Component.newline())
            .append(line(width))
            .build()
    }

    private fun renderHeader(page: Int, pages: Int): Component {
        return Component.text()
            .append(Component.space())
            .append(title)
            .append(Component.space())
            .append(Component.text("(", NamedTextColor.GRAY))
            .append(Component.text(page, NamedTextColor.WHITE))
            .append(Component.text("/", NamedTextColor.GRAY))
            .append(Component.text(pages, NamedTextColor.WHITE))
            .append(Component.text(")", NamedTextColor.GRAY))
            .append(Component.space())
            .build()
    }

    private fun renderFooter(page: Int, pages: Int): Component {
        if (pages == 1)
            return line(width)

        val buttons = Component.text()
        if (page > 1) {
            buttons.append(renderButton('«', NamedTextColor.RED, "Previous Page", page - 1))
            if (page < pages)
                buttons.append(line(8))
        }
        if (page < pages)
            buttons.append(renderButton('»', NamedTextColor.GREEN, "Next Page", page + 1))

        return centered(buttons.build())
    }

    private fun renderButton(character: Char, color: NamedTextColor, hover: String, targetPage: Int): Component {
        return Component.text()
            .append(Component.space())
            .append(Component.text("[", NamedTextColor.GRAY))
            .append(
                Component.text(character, color)
                    .clickEvent(ClickEvent.runCommand(pageCommand(targetPage)))
                    .hoverEvent(HoverEvent.showText(Component.text(hover, color)))
            )
            .append(Component.text("]", NamedTextColor.GRAY))
            .append(Component.space())
            .build()
    }

    private fun centered(component: Component): Component {
        val dashes = line((width - length(component)) / 2)
        return Component.text()
            .append(dashes)
            .append(component)
            .append(dashes)
            .build()
    }

    private fun line(characters: Int): Component =
        Component.text("-".repeat(characters.coerceAtLeast(0)), NamedTextColor.DARK_GRAY)

    companion object {
        fun length(component: Component): Int {
            return (if (component is TextComponent) component.content().length else 0) +
                component.children().sumOf { length(it) }
        }
    }
}
