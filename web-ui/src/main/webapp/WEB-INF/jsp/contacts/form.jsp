<fieldset>
    <div class="control-group">
        <form:label path="firstName" cssClass="control-label">First Name</form:label>
        <div class="controls">
            <form:input path="firstName"/>
            <form:errors path="firstName" cssClass="help-inline"/>
        </div>
    </div>
    <div class="control-group">
        <form:label path="lastName" cssClass="control-label">Last Name</form:label>
        <div class="controls">
            <form:input path="lastName"/>
            <form:errors path="lastName" cssClass="help-inline"/>
        </div>
    </div>
    <div class="control-group">
        <form:label path="phoneNumber" cssClass="control-label">Phone Number</form:label>
        <div class="controls">
            <form:input path="phoneNumber"/>
            <form:errors path="phoneNumber" cssClass="help-inline"/>
        </div>
    </div>
    <div class="control-group">
        <form:label path="street" cssClass="control-label">Street</form:label>
        <div class="controls">
            <form:input path="street"/>
            <form:errors path="street" cssClass="help-inline"/>
        </div>
    </div>
    <div class="control-group">
        <form:label path="city" cssClass="control-label">City</form:label>
        <div class="controls">
            <form:input path="city"/>
            <form:errors path="city" cssClass="help-inline"/>
        </div>
    </div>
    <div class="control-group">
        <form:label path="zipCode" cssClass="control-label">Zip Code</form:label>
        <div class="controls">
            <form:input path="zipCode"/>
            <form:errors path="zipCode" cssClass="help-inline"/>
        </div>
    </div>

    <div class="span8">
        <input type="submit" name="submit" value="Submit" class="btn btn-primary"/>
    </div>
</fieldset>

