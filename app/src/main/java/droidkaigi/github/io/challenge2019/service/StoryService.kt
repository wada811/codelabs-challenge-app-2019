package droidkaigi.github.io.challenge2019.service

import droidkaigi.github.io.challenge2019.domain.Story
import droidkaigi.github.io.challenge2019.infra.repository.StoryRepository

class StoryService(
    private val storyRepository: StoryRepository
) {
    fun getTopStories() = storyRepository.getTopStories()
    fun getStory(id: Long) = storyRepository.getStory(id)
    fun getComments(story: Story) = storyRepository.getComments(story)
    fun saveReadStatus(id: Long) = storyRepository.saveReadStatus(id)
}
