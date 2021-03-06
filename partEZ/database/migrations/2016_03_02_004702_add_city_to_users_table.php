<?php

use Illuminate\Database\Migrations\Migration;

class AddCityToUsersTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        if (!Schema::hasColumn('users', 'city'))
        {
            Schema::table('users', function ($table) {
                $table->string('city');
            });
        }
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        if (Schema::hasColumn('users', 'city')) {
            Schema::table('users', function ($table) {
                $table->dropColumn('city');
            });
        }
    }
}
