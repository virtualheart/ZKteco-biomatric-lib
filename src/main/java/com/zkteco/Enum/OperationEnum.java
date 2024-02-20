package com.zkteco.Enum;

public enum OperationEnum {
    start_up("0"),
    shutdown("1"),
    validation_failure("2"),
    alarm("3"),
    enter_the_menu("4"),
    change_settings("5"),
    registration_fingerprint("6"),
    registration_password("7"),
    card_registration("8"),
    delete_user("9"),
    delete_fingerprints("10"),
    delete_the_password("11"),
    delete_rf_card("12"),
    remove_data("13"),
    mf_create_cards("14"),
    mf_registration_cards("15"),
    mf_registration_cards_2("16"),
    mf_registration_card_deleted("17"),
    mf_clearance_card_content("18"),
    moved_to_the_registration_card_data("19"),
    the_data_in_the_card_copied_to_the_machine("20"),
    set_time("21"),
    restore_factory_settings("22"),
    delete_records_access("23"),
    remove_administrator_rights("24"),
    group_set_up_to_amend_access("25"),
    modify_user_access_control_settings("26"),
    access_time_to_amend_paragraph("27"),
    amend_unlock_portfolio("28"),
    unlock("29"),
    registration_of_new_users("30"),
    fingerprint_attribute_changes("31"),
    stress_alarm("32");

    private final String value;

    OperationEnum(String value) {
        this.value = value;
    }


	public static UnknowableEnum _missing_(String value) {
        return UnknowableEnum.unknown;
    }
}