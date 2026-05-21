package com.turnforge.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.turnforge.model.combat.CombatAction
import com.turnforge.model.combat.CombatState

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromCombatActionList(list: List<CombatAction>?): String {
        val array = JsonArray()
        list?.forEach { action ->
            val wrapper = JsonObject()
            wrapper.addProperty("type", action.javaClass.simpleName)
            wrapper.add("payload", gson.toJsonTree(action))
            array.add(wrapper)
        }
        return array.toString()
    }

    @TypeConverter
    fun toCombatActionList(value: String?): List<CombatAction> {
        if (value.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<CombatAction>()
        try {
            val array = JsonParser.parseString(value).asJsonArray
            array.forEach { element ->
                val wrapper = element.asJsonObject
                val type = wrapper.get("type").asString
                val payload = wrapper.get("payload")

                val action = when (type) {
                    "Attack" -> gson.fromJson(payload, CombatAction.Attack::class.java)
                    "Spell" -> gson.fromJson(payload, CombatAction.Spell::class.java)
                    "Defense" -> gson.fromJson(payload, CombatAction.Defense::class.java)
                    "DiceRoll" -> gson.fromJson(payload, CombatAction.DiceRoll::class.java)
                    else -> null
                }
                action?.let { list.add(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    @TypeConverter
    fun fromCombatState(state: CombatState?): String {
        return gson.toJson(state)
    }

    @TypeConverter
    fun toCombatState(value: String?): CombatState? {
        if (value.isNullOrEmpty()) return null
        return gson.fromJson(value, CombatState::class.java)
    }
}
