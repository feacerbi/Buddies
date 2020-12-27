package com.buddies.profile.model

import com.buddies.common.model.ShareInfoType.NAME
import com.buddies.profile.R

class NameInfoField(
    name: String
) : ContactInfoField(NAME, 0, name, R.drawable.ic_baseline_person)
