package com.buddies.contact.model

import com.buddies.common.model.InfoType.NAME
import com.buddies.contact.R

class NameInfoField(
    name: String
) : ContactInfoField(NAME, 0, name, R.drawable.ic_baseline_person)
