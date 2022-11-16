function change(action, form, mode, mv) {
	action.form[action.form.id + ':' + mode].value = mv;
	return true;
}