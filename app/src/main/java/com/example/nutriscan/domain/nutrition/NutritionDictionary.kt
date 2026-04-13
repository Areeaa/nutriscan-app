package com.example.nutriscan.domain.nutrition

object NutritionDictionary {

    val nutrientAliases: Map<String, List<String>> = mapOf(

        "Energi Total" to listOf(
            // Indonesia
            "energi total",
            "total energi",
            "jumlah energi",
            "energi",
            "kalori total",
            "total kalori",
            "kalori",
            // English
            "total energy",
            "energy total",
            "calories",
            "total calories",
            "calorie",
            "kcal total",
            // OCR typo variants
            "energ1 total",
            "energi tot",
            "total energ"
        ),

        "Energi dari Lemak" to listOf(
            // Indonesia
            "energi dari lemak",
            "kalori dari lemak",
            "energi lemak",
            // English
            "energy from fat",
            "calories from fat",
            "cal from fat",
            // OCR typo variants
            "energi dar lemak",
            "energi dr lemak",
            "energ dari lemak"
        ),

        "Lemak Total" to listOf(
            // Indonesia
            "lemak total",
            "total lemak",
            "jumlah lemak",
            "lemak",
            // English
            "total fat",
            "fat total",
            "fat",
            // OCR typo variants
            "lemak tot",
            "total lmak",
            "lemak tota"
        ),

        "Lemak Jenuh" to listOf(
            // Indonesia
            "lemak jenuh",
            "jenuh",
            "lemak jenuh total",
            "total lemak jenuh",
            // English
            "saturated fat",
            "saturated",
            "sat fat",
            "sat. fat",
            // OCR typo variants
            "lemak jenh",
            "lemak jenu",
            "saturated fa"
        ),

        "Lemak Tidak Jenuh" to listOf(
            // Indonesia
            "lemak tidak jenuh",
            "lemak tak jenuh",
            "tidak jenuh",
            // English
            "unsaturated fat",
            "unsaturated",
            "trans fat",
            "lemak trans"
        ),

        "Karbohidrat Total" to listOf(
            // Indonesia
            "karbohidrat total",
            "total karbohidrat",
            "jumlah karbohidrat",
            "karbohidrat",
            "karbo total",
            "karbo",
            // English
            "total carbohydrate",
            "total carbohydrates",
            "carbohydrate",
            "carbohydrates",
            "carbs",
            "total carbs",
            // OCR typo variants
            "karbohidrat tot",
            "karbohidra total",
            "total karbohidr",
            "karbohidtat total"
        ),

        "Gula" to listOf(
            // Indonesia
            "gula",
            "gula total",
            "total gula",
            "kandungan gula",
            "gula tambahan",
            // English
            "sugar",
            "sugars",
            "total sugar",
            "total sugars",
            "added sugar",
            "added sugars",
            // OCR typo variants
            "gul4",
            "gula tot",
            "sug4r",
            "sug ar"
        ),

        "Serat" to listOf(
            // Indonesia
            "serat",
            "serat pangan",
            "serat makanan",
            "total serat",
            "serat total",
            "serat kasar",
            // English
            "dietary fiber",
            "fibre",
            "fiber",
            "total fiber",
            "dietary fibre",
            // OCR typo variants
            "serat panga",
            "serat panga",
            "dietary fib"
        ),

        "Protein" to listOf(
            // Indonesia
            "protein",
            "total protein",
            "protein total",
            "kandungan protein",
            // English
            "protein",
            "proteins",
            "total protein",
            // OCR typo variants
            "prote1n",
            "prot ein",
            "proteín"
        ),

        "Natrium" to listOf(
            // Indonesia
            "natrium",
            "garam",
            "garam (natrium)",
            "natrium (garam)",
            "kandungan natrium",
            "total natrium",
            "natrium total",
            // English
            "sodium",
            "salt",
            "salt (sodium)",
            "sodium (salt)",
            "total sodium",
            "sodium total",
            // OCR typo variants
            "natr1um",
            "natriu",
            "sod1um",
            "natrium(garam)",
            "garam(natrium)"
        ),

        "Kolesterol" to listOf(
            // Indonesia
            "kolesterol",
            "total kolesterol",
            "kolesterol total",
            // English
            "cholesterol",
            "total cholesterol",
            "chol",
            // OCR typo variants
            "kolestero",
            "kolesteral",
            "cholestero"
        )
    )

    val primaryFields = listOf(
        "Energi Total",
        "Energi dari Lemak",
        "Lemak Total",
        "Lemak Jenuh",
        "Karbohidrat Total",
        "Gula",
        "Protein",
        "Natrium"
    )
}