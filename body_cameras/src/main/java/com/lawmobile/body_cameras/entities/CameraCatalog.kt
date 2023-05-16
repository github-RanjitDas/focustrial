package com.lawmobile.body_cameras.entities

import com.lawmobile.body_cameras.x1.entities.X1CatalogTypes
import com.safefleet.mobile.kotlin_commons.helpers.Result

class CameraCatalog(
    val id: String,
    val name: String,
    val type: String?,
    var order: Int = 0
) {
    companion object {
        fun createInstanceListWithStringX1(value: String): Result<List<CameraCatalog>> {
            return try {
                var catalogList =
                    arrayListOf<CameraCatalog>()
                val params = separateValuesByString(value.replace("\r", ""), "\n")
                var section = 0
                var paramType = X1CatalogTypes.EVENT.value

                params.forEach { param ->
                    val paramValues = separateValuesByString(param, "--")

                    if (isNextSection(param)) {
                        section++
                        paramType = when (section) {
                            1 -> X1CatalogTypes.RACE.value
                            else -> X1CatalogTypes.GENDER.value
                        }
                    } else if (param.isNotBlank()) {
                        catalogList.add(
                            CameraCatalog(
                                paramValues[0],
                                paramValues[1],
                                paramType
                            )
                        )
                    }
                }

                if (catalogList.isNotEmpty()) {
                    catalogList = catalogList.filter { it.type == X1CatalogTypes.EVENT.value } as ArrayList<CameraCatalog>
                    Result.Success(catalogList)
                } else {
                    Result.Error(Exception("The catalog is empty"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

        private fun separateValuesByString(value: String, delimiter: String): List<String> =
            value.split(delimiter)

        private fun isNextSection(value: String) = value.contains("----------------------")
    }
}
