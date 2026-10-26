package com.fathirverse.theme.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.fathirverse.uikit.components.atoms.BackButton
import com.fathirverse.uikit.components.organisms.AppBar
import com.fathirverse.uikit.components.organisms.AppBarBackground

private const val DATA_QURAN_URL = "https://github.com/mamun-al-abdullah/quran"
private const val LPMQ_URL = "https://quran.kemenag.go.id"
private const val CC_LICENSE_URL = "https://creativecommons.org/licenses/by-nc-nd/4.0/"

@Composable
fun DataSourceScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val linkColor = MaterialTheme.colorScheme.primary

    val sections = remember(linkColor) {
        dataSourceSections(linkColor)
    }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        AppBar(
            title = "Sumber data",
            background = AppBarBackground.Primary,
            navigationIcon = {
                BackButton(onClick = onBackClick)
            },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 20.dp,
                    vertical = 20.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            sections.forEach { (title, body) ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(
                modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars),
            )
        }
    }
}

private fun dataSourceSections(linkColor: Color): List<Pair<String, AnnotatedString>> {
    val linkStyles = TextLinkStyles(
        style = SpanStyle(
            color = linkColor,
            textDecoration = TextDecoration.Underline,
        ),
    )

    fun AnnotatedString.Builder.link(label: String, url: String) {
        withLink(LinkAnnotation.Url(url = url, styles = linkStyles)) {
            append(label)
        }
    }

    return listOf(
        "Asal data" to buildAnnotatedString {
            append(
                "Teks Arab, transliterasi, terjemahan Indonesia, dan catatan kaki dalam aplikasi ini " +
                    "diambil dari repositori ",
            )
            link("data-quran", DATA_QURAN_URL)
            append(
                ", bukan diambil langsung oleh pengembang aplikasi dari situs Kementerian Agama. " +
                    "Repositori itu mengumpulkan datanya dari berbagai sumber, di antaranya " +
                    "Lajnah Pentashihan Mushaf Al-Qur'an (LPMQ), Kementerian Agama Republik Indonesia (",
            )
            link("quran.kemenag.go.id", LPMQ_URL)
            append(").")
        },
        "Hak cipta" to buildAnnotatedString {
            append(
                "Hak atas teks dan terjemahan itu tetap milik LPMQ dan pemilik aslinya. " +
                    "Aplikasi ini bukan aplikasi resmi dan tidak berafiliasi dengan " +
                    "Kementerian Agama atau LPMQ.",
            )
        },
        "Lisensi data" to buildAnnotatedString {
            append("Repositori data-quran berlisensi ")
            link("CC BY-NC-ND 4.0", CC_LICENSE_URL)
            append(
                ". Aplikasi ini hanya mengubah format data ke SQLite tanpa mengubah isinya, " +
                    "dan tidak digunakan secara komersial: tanpa iklan dan tanpa pembelian dalam aplikasi.",
            )
        },
    )
}
