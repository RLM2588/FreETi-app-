package com.example.freeti

object PollsRepository {
    val polls: MutableList<Poll> = mutableListOf()

    // Чтобы запретить повторное голосование, храним Set "id-опроса + id-устройства"
    // (для простоты просто id опроса, т.к. нет системы юзеров)
    val votedPollIds: MutableSet<String> = mutableSetOf()

    fun addPoll(poll: Poll) {
        polls.add(poll)
    }

    fun deletePoll(pollId: String) {
        polls.removeAll { it.id == pollId }
    }

    fun vote(pollId: String, optionId: String): Boolean {
        if (votedPollIds.contains(pollId)) return false // уже голосовал
        val poll = polls.find { it.id == pollId } ?: return false
        val option = poll.options.find { it.id == optionId } ?: return false
        option.votes++
        votedPollIds.add(pollId)
        return true
    }
}