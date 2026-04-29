package com.example.nutriscan.domain.nutrition

object NutritionDictionary {

    val nutrientAliases: Map<String, List<String>> = mapOf(

        "Energi Total" to listOf(
            "energi total",
            "total energi",
            "jumlah energi",
            "kalori total",
            "total kalori",
            "total energy",
            "energy total",
            "total calories",
            "calories",
            "calorie",
            "energi",           // cukup aman: satu-satunya field dengan unit kkal
            "energy",
            "kalori",
            // OCR typo
            "energ1 total",
            "energi tot",
            "total energ",
            "kcal total"
        ),

        // ── "Energi dari Lemak" HARUS sebelum "Lemak Total"
        // ── agar sliding-window match "energi" tidak dirampas "Lemak Total"
        "Energi dari Lemak" to listOf(
            "energi dari lemak",
            "kalori dari lemak",
            "energi lemak",
            "energy from fat",
            "calories from fat",
            "cal from fat",
            // OCR typo
            "energi dar lemak",
            "energi dr lemak",
            "energ dari lemak",
            "energy fr fat"
        ),

        // ── "Lemak Jenuh" HARUS sebelum "Lemak Total"
        // ── agar baris "lemak jenuh" tidak juga mencocokkan "Lemak Total"
        "Lemak Jenuh" to listOf(
            "lemak jenuh total",
            "total lemak jenuh",
            "lemak jenuh",
            "saturated fat",
            "saturated",
            "sat. fat",
            "sat fat",
            // OCR typo
            "lemak jenh",
            "lemak jenu",
            "saturated fa",
            "lmak jenuh",
            "lemak jenu h"
        ),

        "Lemak Tidak Jenuh" to listOf(
            "lemak tidak jenuh",
            "lemak tak jenuh",
            "tidak jenuh",
            "lemak trans",
            "unsaturated fat",
            "unsaturated",
            "trans fat",
            // OCR typo
            "lemak tdk jenuh",
            "trans fa"
        ),

        // ── Baru setelah "Lemak Jenuh" dan "Energi dari Lemak" didefinisikan,
        // ── alias pendek "lemak" dan "fat" aman dipakai HANYA jika baris tidak
        // ── lebih cocok ke alias di atas (dijaga oleh logika best-match di ViewModel).
        "Lemak Total" to listOf(
            "lemak total",
            "total lemak",
            "jumlah lemak",
            "total fat",
            "fat total",
            // Alias pendek — tetap ada sebagai fallback, dijaga best-match
            "lemak",
            "fat",
            // OCR typo
            "lemak tot",
            "total lmak",
            "lemak tota",
            "lemak tot al",
            "total fa t"
        ),

        "Karbohidrat Total" to listOf(
            "karbohidrat total",
            "total karbohidrat",
            "jumlah karbohidrat",
            "total carbohydrate",
            "total carbohydrates",
            "carbohydrate total",
            "carbohydrates total",
            // Alias pendek — dijaga best-match
            "karbohidrat",
            "carbohydrate",
            "carbohydrates",
            "karbo total",
            "total carbs",
            // OCR typo
            "karbohidrat tot",
            "karbohidra total",
            "total karbohidr",
            "karbohidtat total",
            "karboh total",
            "carbohydrat total"
        ),

        "Gula" to listOf(
            "gula total",
            "total gula",
            "gula tambahan",
            "kandungan gula",
            "total sugar",
            "total sugars",
            "added sugar",
            "added sugars",
            // Alias pendek
            "gula",
            "sugar",
            "sugars",
            // OCR typo
            "gul4",
            "gula tot",
            "sug4r",
            "sug ar"
        ),

        "Serat" to listOf(
            "serat pangan",
            "serat makanan",
            "serat total",
            "total serat",
            "serat kasar",
            "dietary fiber",
            "dietary fibre",
            "total fiber",
            "total fibre",
            // Alias pendek
            "serat",
            "fiber",
            "fibre",
            // OCR typo
            "serat panga",
            "dietary fib",
            "serat panga n"
        ),

        "Protein" to listOf(
            "protein total",
            "total protein",
            "kandungan protein",
            // Alias pendek — unik, tidak muncul di alias lain
            "protein",
            "proteins",
            // OCR typo
            "prote1n",
            "prot ein",
            "proteín",
            "prot ein"
        ),

        "Natrium" to listOf(
            "natrium total",
            "total natrium",
            "garam (natrium)",
            "natrium (garam)",
            "kandungan natrium",
            "garam(natrium)",
            "natrium(garam)",
            "sodium total",
            "total sodium",
            "salt (sodium)",
            "sodium (salt)",
            // Alias pendek
            "natrium",
            "sodium",
            "garam",
            "salt",
            // OCR typo
            "natr1um",
            "natriu",
            "sod1um",
            "sod ium"
        ),

        "Kolesterol" to listOf(
            "kolesterol total",
            "total kolesterol",
            "total cholesterol",
            "cholesterol total",
            // Alias pendek
            "kolesterol",
            "cholesterol",
            "chol",
            // OCR typo
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