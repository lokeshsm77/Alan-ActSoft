const OPTIONS = [
    ["Materials Request Form", "Materials Request", "Materials Form", "Request Form", "Materials"],
    ["Time", "Time Keeping", "Time Keeper", "Timing"],
    ["COVID Test Form", "COVID Test", "Coronavirus Test", "Worker Test"]
]

const SYMPTOMS = ["Fever", "Coughing", "Vomiting", "None of above"];

const MR_INTENT = OPTIONS[0].join('|')
const TIME_INTENT = OPTIONS[1].join('|')
const COVID_INTENT = OPTIONS[2].join('|')
const SYMPTOMS_INTENT = SYMPTOMS.join('|');

let timeContext = context(() => {
    follow(`(Clock|Check) in`, `(Just) came in`, `(Just) get in`, `(Just) walked in`, p => {
        if(p.state.clocked_in) p.play(`You are already clocked in`);
        else {
            p.state = {}; //Check if this is correct way to reset state
            var timeStamp = api.moment().tz(p.timeZone).format("h:mmA");
            p.play(`(Okay|Good), you have clocked in at ` + timeStamp + `. Make sure to fill out the COVID Test Form after you're done! Say Start COVID test to begin.`);
            p.play({command: "clock_in", time: timeStamp})
            p.play({command: 'showForm', embeddedPage: true, page: "actsoft_example.html",
                    formKey: "Clock In", formValue: timeStamp});
            p.state.clocked_in = true;
        }
    });

    follow(`(Clock|Check) out`, p => {
        if(p.state.clocked_in) {
            var timeStamp = api.moment().tz(p.timeZone).format("h:mmA");
            p.play(`(Okay|Good), you have clocked out at ` + timeStamp);
            p.play({command: "clock_out", time: timeStamp})
            p.play({command: 'showForm', embeddedPage: true, page: "actsoft_example.html",
                    formKey: "Clock Out", formValue: timeStamp});
            p.state.clocked_in = false;
        } else p.play(`Please clock in first`);
    });

    intent(`(Start|Commence|Take) break`, p => {
        if(p.state.break_in) p.play(`You are already in break`);
        else if(p.state.clocked_in) {
            var timeStamp = api.moment().tz(p.timeZone).format("h:mmA");
            p.play(`(Okay|Good), you have started your break at ` + timeStamp);
            p.play({command: "break_in", time: timeStamp})
            p.play({command: 'showForm', embeddedPage: true, page: "actsoft_example.html",
                    formKey: "Start Break", formValue: timeStamp});
            p.state.break_in = true;
        } else p.play(`Please clock in first`);
    });

    follow(`(Stop|End|Finish) break`, p => {
        if(p.state.break_in) {
            var timeStamp = api.moment().tz(p.timeZone).format("h:mmA");
            p.play(`(Okay|Good), you have ended break at ` + timeStamp);
            p.play({command: "break_out", time: timeStamp})
            p.play({command: 'showForm', embeddedPage: true, page: "actsoft_example.html",
                    formKey: "End Break", formValue: timeStamp});
            p.state.break_in = false;
        } else p.play(`Please start your break first`);
    });

    follow(`(Start|Commence|Take) lunch (break|)`, p => {
        if(p.state.lunch_in) p.play(`You are already in your lunch break`);
        else if(p.state.clocked_in && !p.state.first_lunch) {
            var timeStamp = api.moment().tz(p.timeZone).format("h:mmA");
            p.play(`(Okay|Good), you have started your lunch break at ` + timeStamp);
            p.play({command: "lunch_in", time: timeStamp})
            p.play({command: 'showForm', embeddedPage: true, page: "actsoft_example.html",
                    formKey: "Start Lunch", formValue: timeStamp});
            p.state.lunch_in = true;
            p.state.first_lunch = true;
        } else if(!p.state.clocked_in) p.play(`Please clock in first`);
        else p.play(`You have already had your lunch break.`)
    });

    follow(`(Stop|End|Finish) lunch (break|)`, p => {
        if(p.state.lunch_in) {
            var timeStamp = api.moment().tz(p.timeZone).format("h:mmA");
            p.play(`(Okay|Good), you have finished your lunch break at ` + timeStamp);
            p.play({command: 'showForm', embeddedPage: true, page: "actsoft_example.html",
                    formKey: "Lunch End", formValue: timeStamp});
            p.play({command: "lunch_out", time: timeStamp})
            p.state.lunch_in = false;
        } else p.play(`Please start your lunch first`);
    });

    follow(`(Done|Finished|Stop|Go back|End|Return|Exit)`, p => {
        p.play({command: "back", screen: "home"})
        p.play({command: 'showScreen', screenText: "Homepage", embeddedPage: true, page: "actsoft_example.html"});
        p.resolve(null)
    });

    follow(`(Show me|Choose|Select|Open|Start|) $(ITEM ${COVID_INTENT})`, p => {
        p.play(`(Opening|Showing) COVID Test Form. What is your name?`)
        p.play({command: 'showScreen', screenText: "COVID Test Form", embeddedPage: true, page: "actsoft_example.html"});
        p.play({command: "open", screen: "covid_test_form"})
        let state = {prevScreen: "Time Keeping"};
        p.then(covidContext, {state: state});
    });

    follow(`What can I do here?`, reply('You can set your workday times here.'));
    fallback("Sorry could you repeat that? You can tell me to clock in or out, take a break, or have lunch!");
})

let covidContext = context(() => {
    follow(`(My name is|Set name as|Put name|Employee Name|) $(NAME)`, p => {
        p.play({command: "employee_name", name: p.NAME.value})
        p.state.name = p.NAME.value;
        p.play('Name recorded. What is your temperature?');
        p.play({command: 'showForm', embeddedPage: true, page: "actsoft_example.html",
                    formKey: "Employee Name", formValue: p.state.name});
    });

    follow(`(Set temperature|Temperature|Put temperature|Temp|) $(NUMBER) (degrees|) (fahrenheit|celsius|)`, p => {
        p.play({command: "temperature", temp: p.NUMBER.value})
        p.state.temp = p.NUMBER.value;
        p.play('Temperature recorded. What are your symptoms?');
        p.play({command: 'showForm', embeddedPage: true, page: "actsoft_example.html",
                    formKey: "Temperature", formValue: p.state.temp});
    });

    follow(`(I have not experienced any symptoms|No symptoms|I do not have symptoms)`,
           `(Have experienced|My symptoms are) $(SYMPTOM ${SYMPTOMS_INTENT})`,
           `(Have experienced|My symptoms are) $(SYMPTOM ${SYMPTOMS_INTENT}) (and|) $(SYMPTOM ${SYMPTOMS_INTENT})`,
           `(Have experienced|My symptoms are) $(SYMPTOM ${SYMPTOMS_INTENT}) (and|) $(SYMPTOM ${SYMPTOMS_INTENT}) (and|) $(SYMPTOM ${SYMPTOMS_INTENT})`,
           p => {
        if(p.SYMPTOMs.length != 0) {
            p.state.symptoms = [];
            for(var i = 0; i < p.SYMPTOMs.length; i++) {
                p.play({command: "symptoms", symptom: p.SYMPTOMs[i].value});
                p.state.symptoms.push(p.SYMPTOMs[i].value);
            }
            p.play(`(Okay|Alright), your symptoms have been recorded. Say submit to finish.`);
        } else {
            p.state.symptoms = "None";
            p.play({command: "symptoms", symptom: p.state.symptoms});

            p.play(`(Okay|Alright), say submit to finish.`);
        }
        p.play({command: 'showForm', embeddedPage: true, page: "actsoft_example.html",
                    formKey: "Symptoms", formValue: p.state.symptoms});
    });

    follow(`None of above`, p => {
        p.state.symptoms = "None of above";
        p.play({command: "symptoms", symptom: p.state.symptoms});
        p.play(`(Okay|Alright), say submit to finish.`);
    });

    follow(`(Submit|Next|Done|Complete|Finished)`, p => {
        var completed = checkStatesCovid(p);
        if(completed) {
            p.play(`Thanks for submitting! Hope you feel better soon.`);
            p.play({command: "submit"})
            if(p.state.prevScreen) {
                p.play('Going back to Time Keeping');
                p.play({command: "back", screen: "time_keeping"});
                p.play({command: 'showScreen', screenText: "Time Keeping", embeddedPage: true, page: "actsoft_example.html"});
            } else {
                p.play({command: "back", screen: "home"});
                p.play({command: 'showScreen', screenText: "Homepage", embeddedPage: true, page: "actsoft_example.html"});
            }
            p.resolve(null);
        }
    });

    follow(`(Stop|Go back|End|Return)`, p => {
        p.play({command: "open", screen: "home"})
        p.play({command: 'showScreen', screenText: "Homepage", embeddedPage: true, page: "actsoft_example.html"});
        p.resolve(null)
    });

    follow(`What can I do here?`, reply('You can answer the COVID testing form here.'));
    fallback("Sorry could you repeat that? You can tell me to put your name, set your temperature, or log your symptoms!");
});

let materialsContext = context(() => {
    follow(`(Set job name|Put job name|Job Name|Record Job Name) (as|is|) $(JOBNAME* (.*))`, p => {
        p.play({command: "job_name", name: p.JOBNAME.value})
        p.state.name = p.JOBNAME.value;
        p.play('Job Name recorded. what is the job number?');
        p.play({command: 'showForm', embeddedPage: true, page: "actsoft_example.html",
                    formKey: "Job Name", formValue: p.state.name});
    });
    follow(`(Set job number as|Put job number|Job Number) $(NUMBER)`, p => {
        p.play({command: "job_number", name: p.NUMBER.number})
        p.state.number = p.NUMBER.number;
        p.play('Job Number recorded. what materials are required?');
        p.play({command: 'showForm', embeddedPage: true, page: "actsoft_example.html",
                    formKey: "Job Number", formValue: p.state.number});
    });
    follow(`(I need|I need the material_|Material_ Needed Are|Material_ are|Material_) (needed|) (are|as|is|) $(MATERIALS* (.*))`, p => {
        p.play({command: "material_needed", materials: p.MATERIALS.value});
        p.state.materials = p.MATERIALS.value;
        p.play('Recorded requested materials. How much quantity you need?');
        p.play({command: 'showForm', embeddedPage: true, page: "actsoft_example.html",
                    formKey: "Requested Materials", formValue: p.state.materials});
    });
    follow(`(Set quantity as|Put quantity|quantity) $(NUMBER)`, p => {
        p.play({command: "quantity", name: p.NUMBER.number})
        p.state.quantity = p.NUMBER.number;
        p.play('Quantity recorded. When do you need this job?');
        p.play({command: 'showForm', embeddedPage: true, page: "actsoft_example.html",
                    formKey: "Quantity", formValue: p.state.quantity});
    });
    follow(`(Needed by|We need by|By) $(DATE)`, p => {
        p.play({command: "needed_by", date: p.DATE.moment})
        p.state.date = p.DATE.moment;
        p.play('Date Received. Say Submit or Done, to finish the request');
        p.play({command: 'showForm', embeddedPage: true, page: "actsoft_example.html",
                    formKey: "Date Needed By", formValue: p.state.date.format()});
    });

    follow(`(Submit|Next|Done|Complete|Finished)`, p => {
        var completed = checkStatesMRForm(p);
        if(completed) {
            p.play(`Thanks! Your form has been submitted`);
            p.play({command: "submit"})
            p.play({command: "back", screen: "home"});
            p.play({command: 'showScreen', screenText: "Homepage", embeddedPage: true, page: "actsoft_example.html"});
            p.resolve(null);
        }
    });

    follow(`(Stop|Go back|End|Return)`, p => { //Perhaps go back for specific intents (go back on the quantity)
        p.play({command: "back", screen: "home"})
        p.play({command: 'showScreen', screenText: "Homepage", embeddedPage: true, page: "actsoft_example.html"});
        p.resolve(null)
    });

    follow(`What can I do here?`, reply('You can request materials for work here.'));
    fallback("Sorry could you repeat that? You can tell me the job name, job number, requested materials, the quantity, and the date!");
});

let genFormContext = context(() => {
    follow(`(Start|) new form`, p => {
        p.play("Starting New Form");
        p.play({command: "open", screen: "material_new_form"});
        p.then(materialsContext);
    });
    follow(`Continue draft`, p => {
        p.play({command: "continue_draft"});
        p.resolve(null); //TODO: I don't know what this flow is part of
    });
    follow(`View Submissions`, p => {
        p.play({command: "view_submissions"});
        p.resolve(null); //TODO: I don't know what this flow is part of
    });
});

intent(`(Show me|Choose|Select|Open|Start|) $(ITEM ${MR_INTENT})`, p => {
    p.play(`(Opening|Showing) Materials Request Form. What is Job Name?`)
    p.play({command: 'showScreen', screenText: "Materials Request Form", embeddedPage: true, page: "actsoft_example.html"});
    p.play({command: "open", screen: "materials_request_form"})
    p.then(materialsContext);
});

intent(`(Show me|Choose|Select|Open|Start|) $(ITEM ${TIME_INTENT})`, p => {
    p.play(`(Opening|Showing) Time Keeping.`)
    p.play({command: 'showScreen', screenText: "Time Keeping", embeddedPage: true, page: "actsoft_example.html"});
    p.play({command: "open", screen: "time_keeping"})
    p.then(timeContext)
});

intent(`(I just walked in|I just came to office|I just came to work)`, p => {
        p.state = {}; //Check if this is correct way to reset state

    p.play(`(Opening|Showing) Time Keeping.`)
    p.play({command: 'showScreen', screenText: "Time Keeping", embeddedPage: true, page: "actsoft_example.html"});
    p.play({command: "open", screen: "time_keeping"})
            var timeStamp = api.moment().tz(p.timeZone).format("h:mmA");
            p.play(`(Okay|Good), you have clocked in at ` + timeStamp + `. Make sure to fill out the COVID Test Form after you're done! Say Start COVID test to begin.`);
            p.play({command: "clock_in", time: timeStamp})
            p.play({command: 'showForm', embeddedPage: true, page: "actsoft_example.html",
                    formKey: "Clock In", formValue: timeStamp});
            p.state.clocked_in = true;
    p.then(timeContext)
});

intent(`(Show me|Choose|Select|Open|Start|) $(ITEM ${COVID_INTENT})`, p => {
    p.play(`(Opening|Showing) COVID Test Form. What is your name?`)
    //p.play({command: 'showScreen', screenText: "COVID Test Form", embeddedPage: true, page: "actsoft_example.html"});
    p.play({command: "open", screen: "covid_test_form"})
    p.then(covidContext)
});

// What can I do here?
intent(`What can I do here?`, reply('(Hello|Hi), you can do time keeping, a COVID test, and a materials request'));

function checkStatesCovid(p) {
    if(!p.state.name) {p.play(`Please input employee name.`); return false;}
    if(!p.state.temp) {p.play(`Please input temperature.`); return false;}
    if(!p.state.symptoms) {p.play(`Please input symptoms.`); return false;}
    return true;
}

function checkStatesMRForm(p) {
    if(!p.state.name) {p.play(`Please input job name.`); return false;}
    if(!p.state.number) {p.play(`Please input job number.`); return false;}
    if(!p.state.materials) {p.play(`Please input materials needed.`); return false;}
    if(!p.state.quantity) {p.play(`Please input quantity.`); return false;}
    if(!p.state.date) {p.play(`Please input date needed by.`); return false;}
    return true;
}

// Materials Request Form: Start new form, continue draft, view submissions (Job Name, Job Number, Material Needed, Quantity, Needed By)
// Time Keeping: Clock in/out, Start/End Break, Start/End Lunch
// COVID: Employee Name, Temperature, Have you experienced any of the following symptoms (Fever, Coughing, Vomiting)
