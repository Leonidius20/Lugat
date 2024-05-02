package io.github.leonidius20.lugat.domain.interactors

/**
 * This class represents the action of opening a word's definition either from a link
 * from a different word's definition, or as part of looking for a selected word
 * when pressing a context menu button (don't forget to trim whitespace in that case)
 *
 * The logic is this - if there is a word like this in the library - open the word's definition,
 * if there's no exact match - open search screen to let user see non-exact matches
 * and select something
 */
class OpenWordDefinitionInteractor {
}