import {Component, computed, inject} from '@angular/core';
import {MatListModule} from '@angular/material/list';
import {MatDividerModule} from '@angular/material/divider';
import {RouterLink, RouterModule} from '@angular/router';
import {Role} from '../../app.constants';
import {AuthService} from '../../services/global/auth.service';

const AppMenuItems: MenuItem[] = [{
  link: '/dashboard',
  title: 'Dashboard',
}, {
  link: '/users',
  title: 'Gebruikers',
  role: Role.Admin
}];

const AppActionItems: MenuItemWithIcon[] = [{
  link: '/tickets/nieuw',
  title: 'Nieuw ticket',
  icon: 'plus'
}]

@Component({
  selector: 'app-nav',
  imports: [
    MatDividerModule,
    MatListModule,
    RouterModule,
    RouterLink,
  ],
  templateUrl: './app-nav.component.html',
  styleUrl: './app-nav.component.css',
})
export class AppNavComponent {
  private readonly authService = inject(AuthService);

  readonly menuItems = computed(() => this.filterAvailable(AppMenuItems));
  readonly menuActions = computed(() => this.filterAvailable(AppActionItems))

  private filterAvailable<Item extends MenuItem>(items: Item[]): Item[] {
    const roles = this.authService.roles ?? [];

    return items.filter(item => {
      if (!item.role)
        return true;

      return roles.includes(item.role);
    })
  }
}

interface MenuItem {
  title: string;
  link: string;
  role?: Role;
}

interface MenuItemWithIcon extends MenuItem {
  icon: string;
}
