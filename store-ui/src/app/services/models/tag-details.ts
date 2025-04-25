export interface TagDetails {
    value?: string;
    severity?: TagSeverity;
    icon?: string
}

type TagSeverity = 'success' | 'info' | 'danger' | 'secondary' | 'contrast' | 'warn' | undefined;
