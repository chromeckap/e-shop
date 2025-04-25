import {Component, OnInit} from '@angular/core';
import {MenuItem} from "primeng/api";
import {AdminSidebarItem} from "../admin-sidebar-item/admin.sidebaritem";

@Component({
    selector: 'admin-sidebar',
    imports: [
        AdminSidebarItem,
    ],
    templateUrl: './admin.sidebar.html',
    standalone: true
})
export class AdminSidebar implements OnInit {
    model: MenuItem[] = [];

    ngOnInit(): void {
        this.model = [
            {
                label: 'Domů',
                items: [
                    {
                        label: 'Administrace',
                        icon: 'pi pi-fw pi-home',
                        routerLink: ['/admin']
                    },
                    {
                        label: 'Zpět do obchodu',
                        icon: 'pi pi-fw pi-sign-out',
                        command: () => window.location.href = '/'
                    }
                ]
            },
            {
                label: 'Katalog',
                items: [
                    {
                        label: 'Kategorie',
                        icon: 'pi pi-fw pi-sitemap',
                        items: [
                            {
                                label: 'Seznam',
                                icon: 'pi pi-fw pi-list',
                                routerLink: ['/admin/kategorie']
                            },
                            {
                                label: 'Vytvořit',
                                icon: 'pi pi-fw pi-plus',
                                routerLink: ['/admin/kategorie/vytvorit']
                            }
                        ]
                    },
                    {
                        label: 'Atributy',
                        icon: 'pi pi-fw pi-id-card',
                        items: [
                            {
                                label: 'Seznam',
                                icon: 'pi pi-fw pi-list',
                                routerLink: ['/admin/atributy']
                            },
                            {
                                label: 'Vytvořit',
                                icon: 'pi pi-fw pi-plus',
                                routerLink: ['/admin/atributy/vytvorit']
                            }
                        ]
                    },
                    {
                        label: 'Produkty',
                        icon: 'pi pi-fw pi-gift',
                        items: [
                            {
                                label: 'Seznam',
                                icon: 'pi pi-fw pi-list',
                                routerLink: ['/admin/produkty']
                            },
                            {
                                label: 'Vytvořit',
                                icon: 'pi pi-fw pi-plus',
                                routerLink: ['/admin/produkty/vytvorit']
                            }
                        ]
                    }
                ]
            },
            {
                label: 'Prodej',
                items: [
                    {
                        label: 'Objednávky',
                        icon: 'pi pi-fw pi-receipt',
                        routerLink: ['/admin/objednavky']
                    },
                    {
                        label: 'Platební metody',
                        icon: 'pi pi-fw pi-wallet',
                        items: [
                            {
                                label: 'Seznam',
                                icon: 'pi pi-fw pi-list',
                                routerLink: ['/admin/platebni-metody']
                            },
                            {
                                label: 'Vytvořit',
                                icon: 'pi pi-fw pi-plus',
                                routerLink: ['/admin/platebni-metody/vytvorit']
                            }
                        ]
                    },
                    {
                        label: 'Dopravní metody',
                        icon: 'pi pi-fw pi-truck',
                        items: [
                            {
                                label: 'Seznam',
                                icon: 'pi pi-fw pi-list',
                                routerLink: ['/admin/dopravni-metody']
                            },
                            {
                                label: 'Vytvořit',
                                icon: 'pi pi-fw pi-plus',
                                routerLink: ['/admin/dopravni-metody/vytvorit']
                            }
                        ]
                    }
                ]
            },


            {
                label: 'Uživatelé',
                items: [
                    {
                        label: 'Uživatelé',
                        icon: 'pi pi-fw pi-user',
                        routerLink: ['/admin/uzivatele']
                    }
                ]
            }
        ]
    }
}
