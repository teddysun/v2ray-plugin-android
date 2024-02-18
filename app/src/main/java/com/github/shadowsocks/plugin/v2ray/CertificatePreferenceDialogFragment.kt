/*******************************************************************************
 *                                                                             *
 *  Copyright (C) 2019 by Max Lv <max.c.lv@gmail.com>                          *
 *  Copyright (C) 2019 by Mygod Studio <contact-shadowsocks-android@mygod.be>  *
 *                                                                             *
 *  This program is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by       *
 *  the Free Software Foundation, either version 3 of the License, or          *
 *  (at your option) any later version.                                        *
 *                                                                             *
 *  This program is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 *  GNU General Public License for more details.                               *
 *                                                                             *
 *  You should have received a copy of the GNU General Public License          *
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.       *
 *                                                                             *
 *******************************************************************************/

package com.github.shadowsocks.plugin.v2ray

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.preference.EditTextPreferenceDialogFragmentCompat
import com.google.android.material.snackbar.Snackbar

class CertificatePreferenceDialogFragment : EditTextPreferenceDialogFragmentCompat() {
    fun setKey(key: String) {
        arguments = bundleOf(Pair(ARG_KEY, key))
    }

    override fun onPrepareDialogBuilder(builder: AlertDialog.Builder) {
        super.onPrepareDialogBuilder(builder)
        builder.setNeutralButton(com.github.shadowsocks.plugin.R.string.browse) { _, _ ->
            val activity = requireActivity()
            try {
                targetFragment!!.startActivityForResult(Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/pkix-cert"
                }, ConfigFragment.REQUEST_BROWSE_CERTIFICATE)
                return@setNeutralButton
            } catch (_: ActivityNotFoundException) { } catch (_: SecurityException) { }
            Snackbar.make(activity.findViewById(R.id.content), com.github.shadowsocks.plugin.R.string.file_manager_missing, Snackbar.LENGTH_SHORT)
                    .show()
        }
    }
}
